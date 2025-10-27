package com.digitaldairy.service;

/**
 * MilkRecordService: Interface for milk record operations.
 */

import com.digitaldairy.dto.response.CsvUploadResponse;
import com.digitaldairy.dto.response.MilkRecordResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MilkRecordService {

    /**
     * Upload and process CSV file with milk records.
     * @param file CSV file from milk analyzer
     * @param dairyCenterId Dairy center ID from JWT
     * @return Upload result with success/failure counts
     */
    CsvUploadResponse uploadCsv(MultipartFile file, Long dairyCenterId);

    /**
     * Get all milk records for a farmer by member code.
     * @param memberCode Farmer's dairy given ID
     * @param dairyCenterId Dairy center ID from JWT
     * @return List of milk records
     */
    List<MilkRecordResponse> getFarmerRecords(String memberCode, Long dairyCenterId);

    /**
     * Get farmer's milk records for a specific Nepali month.
     * @param memberCode Farmer's dairy given ID
     * @param nepaliMonth Nepali month (e.g., "07")
     * @param nepaliYear Nepali year (e.g., "2082")
     * @param dairyCenterId Dairy center ID from JWT
     * @return List of milk records
     */
    List<MilkRecordResponse> getFarmerRecordsByNepaliMonth(
            String memberCode, String nepaliMonth, String nepaliYear, Long dairyCenterId);

    /**
     * Get all milk records for a dairy center by Nepali month.
     * @param nepaliMonth Nepali month
     * @param nepaliYear Nepali year
     * @param dairyCenterId Dairy center ID from JWT
     * @return List of milk records
     */
    List<MilkRecordResponse> getDairyRecordsByNepaliMonth(
            String nepaliMonth, String nepaliYear, Long dairyCenterId);
}