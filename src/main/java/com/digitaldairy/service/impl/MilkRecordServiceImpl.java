package com.digitaldairy.service.impl;

/**
 * MilkRecordServiceImpl: Implements milk record CSV processing and queries.
 * Handles batch processing, validation, and error collection.
 */

import com.digitaldairy.dto.response.CsvUploadResponse;
import com.digitaldairy.dto.response.MilkRecordResponse;
import com.digitaldairy.exception.CsvProcessingException;
import com.digitaldairy.exception.TenantNotFoundException;
import com.digitaldairy.model.DairyCenter;
import com.digitaldairy.model.Farmer;
import com.digitaldairy.model.MilkRecord;
import com.digitaldairy.repository.DairyCenterRepository;
import com.digitaldairy.repository.FarmerRepository;
import com.digitaldairy.repository.MilkRecordRepository;
import com.digitaldairy.service.MilkRecordService;
import com.digitaldairy.util.CsvParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MilkRecordServiceImpl implements MilkRecordService {

    private final MilkRecordRepository milkRecordRepository;
    private final FarmerRepository farmerRepository;
    private final DairyCenterRepository dairyCenterRepository;
    private final CsvParser csvParser;

    private static final int BATCH_SIZE = 50;

    @Override
    public CsvUploadResponse uploadCsv(MultipartFile file, Long dairyCenterId) {
        log.info("Processing CSV upload for dairy center: {}", dairyCenterId);

        // Validate file
        if (file == null || file.isEmpty()) {
            throw new CsvProcessingException("CSV file is empty");
        }

        // Validate dairy center exists
        DairyCenter dairyCenter = dairyCenterRepository.findById(dairyCenterId)
                .orElseThrow(() -> new TenantNotFoundException(dairyCenterId));

        // Parse CSV
        List<CsvParser.ParsedMilkRecord> parsedRecords;
        try {
            parsedRecords = csvParser.parseCsvFile(file);
        } catch (Exception e) {
            log.error("Failed to parse CSV file", e);
            throw new CsvProcessingException("Failed to parse CSV file: " + e.getMessage(), e);
        }

        if (parsedRecords.isEmpty()) {
            throw new CsvProcessingException("CSV file contains no valid records");
        }

        // Process records in batches (no farmer validation needed)
        CsvUploadResponse response = processBatches(parsedRecords, dairyCenter);

        log.info("CSV processing complete: total={}, success={}, failed={}",
                response.getTotalRecords(), response.getSuccessfulRecords(), response.getFailedRecords());

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MilkRecordResponse> getFarmerRecords(String memberCode, Long dairyCenterId) {
        log.debug("Fetching all records for farmer: memberCode={}, dairyCenterId={}",
                memberCode, dairyCenterId);

        List<MilkRecord> records = milkRecordRepository.findByMemberCodeAndDairyCenterId(
                memberCode, dairyCenterId);

        return records.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MilkRecordResponse> getFarmerRecordsByNepaliMonth(
            String memberCode, String nepaliMonth, String nepaliYear, Long dairyCenterId) {
        log.debug("Fetching farmer records: memberCode={}, month={}, year={}, dairyCenterId={}",
                memberCode, nepaliMonth, nepaliYear, dairyCenterId);

        List<MilkRecord> records = milkRecordRepository
                .findByMemberCodeAndDairyCenterIdAndNepaliMonthAndNepaliYear(
                        memberCode, dairyCenterId, nepaliMonth, nepaliYear);

        return records.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MilkRecordResponse> getDairyRecordsByNepaliMonth(
            String nepaliMonth, String nepaliYear, Long dairyCenterId) {
        log.debug("Fetching dairy records: month={}, year={}, dairyCenterId={}",
                nepaliMonth, nepaliYear, dairyCenterId);

        List<MilkRecord> records = milkRecordRepository
                .findByDairyCenterIdAndNepaliMonthAndNepaliYear(
                        dairyCenterId, nepaliMonth, nepaliYear);

        return records.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Load all farmers for a dairy center into a map for quick lookup.
     */
    private Map<String, Farmer> loadFarmersMap(Long dairyCenterId) {
        List<Farmer> farmers = farmerRepository.findByDairyCenterId(dairyCenterId);
        log.debug("Loaded {} farmers for dairy center {}", farmers.size(), dairyCenterId);

        return farmers.stream()
                .collect(Collectors.toMap(
                        Farmer::getDairyGivenId,
                        farmer -> farmer,
                        (existing, replacement) -> existing  // Keep first if duplicates
                ));
    }

    /**
     * Process parsed records in batches.
     * No farmer validation - records stored by memberCode.
     * Farmers can register later and see their historical data.
     * Allows multiple records per day (morning/evening shifts).
     */
    private CsvUploadResponse processBatches(
            List<CsvParser.ParsedMilkRecord> parsedRecords,
            DairyCenter dairyCenter) {

        int totalRecords = parsedRecords.size();
        int successCount = 0;
        List<CsvUploadResponse.ErrorDetail> errors = new ArrayList<>();

        List<MilkRecord> batch = new ArrayList<>(BATCH_SIZE);

        for (CsvParser.ParsedMilkRecord parsed : parsedRecords) {
            try {
                // Skip if parsing already failed
                if (parsed.hasError()) {
                    errors.add(new CsvUploadResponse.ErrorDetail(
                            parsed.getRowNumber(), parsed.getError()));
                    continue;
                }

                // Create MilkRecord entity - NO VALIDATION
                // - No farmer check (can register later)
                // - No duplicate check (morning/evening shifts allowed)
                MilkRecord record = new MilkRecord(
                        parsed.getCollectionDate(),
                        parsed.getNepaliDate(),
                        parsed.getNepaliMonth(),
                        parsed.getNepaliYear(),
                        parsed.getCollectionTime(),
                        parsed.getMemberCode(),
                        parsed.getVolumeLiters(),
                        parsed.getFatPercentage(),
                        parsed.getSnf(),
                        parsed.getRate(),
                        parsed.getAmount(),
                        parsed.getRemarks(),
                        dairyCenter
                );

                batch.add(record);
                successCount++;

                // Save batch when it reaches BATCH_SIZE
                if (batch.size() >= BATCH_SIZE) {
                    milkRecordRepository.saveAll(batch);
                    log.debug("Saved batch of {} records", batch.size());
                    batch.clear();
                }

            } catch (Exception e) {
                log.warn("Failed to process row {}: {}", parsed.getRowNumber(), e.getMessage());
                errors.add(new CsvUploadResponse.ErrorDetail(
                        parsed.getRowNumber(),
                        "Processing failed: " + e.getMessage()));
            }
        }

        // Save remaining records in batch
        if (!batch.isEmpty()) {
            milkRecordRepository.saveAll(batch);
            log.debug("Saved final batch of {} records", batch.size());
        }

        int failedCount = totalRecords - successCount;

        return new CsvUploadResponse(totalRecords, successCount, failedCount, errors);
    }

    /**
     * Map MilkRecord entity to response DTO.
     */
    private MilkRecordResponse mapToResponse(MilkRecord record) {
        // Get farmer name from repository
        String farmerName = farmerRepository.findByDairyGivenIdAndDairyCenterId(
                        record.getMemberCode(), record.getDairyCenterId())
                .map(Farmer::getName)
                .orElse("Unknown");

        return new MilkRecordResponse(
                record.getId(),
                record.getCollectionDate(),
                record.getNepaliDate(),
                record.getNepaliMonth(),
                record.getNepaliYear(),
                record.getCollectionTime(),
                record.getMemberCode(),
                farmerName,
                record.getVolumeLiters(),
                record.getFatPercentage(),
                record.getSnf(),
                record.getRate(),
                record.getAmount(),
                record.getRemarks(),
                record.getDairyCenterId(),
                record.getDairyCenter().getName(),
                record.getCreatedAt()
        );
    }
}