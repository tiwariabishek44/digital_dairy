package com.digitaldairy.util;

/**
 * CsvParser: Utility class for parsing milk analyzer CSV files.
 * Handles date/time conversions, Nepali date extraction, and data validation.
 */

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class CsvParser {

    // Date formatters - Fixed formats from milk analyzer
    private static final DateTimeFormatter COLL_DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy");  // Coll_date: 10/26/2025
    private static final DateTimeFormatter NEPALI_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy"); // Ne_date: 09/07/2082
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Parse CSV file and return list of parsed records.
     * Each record is a ParsedMilkRecord object with validated data.
     */
    public List<ParsedMilkRecord> parseCsvFile(MultipartFile file) throws Exception {
        List<ParsedMilkRecord> records = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
             CSVParser csvParser = new CSVParser(reader,
                     CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {

            for (CSVRecord csvRecord : csvParser) {
                try {
                    ParsedMilkRecord record = parseRecord(csvRecord);
                    records.add(record);
                } catch (Exception e) {
                    log.warn("Failed to parse row {}: {}", csvRecord.getRecordNumber(), e.getMessage());
                    // Create error record to track failures
                    ParsedMilkRecord errorRecord = new ParsedMilkRecord();
                    errorRecord.setRowNumber((int) csvRecord.getRecordNumber());
                    errorRecord.setError(e.getMessage());
                    records.add(errorRecord);
                }
            }
        }

        log.info("Parsed {} records from CSV", records.size());
        return records;
    }

    /**
     * Parse single CSV record into ParsedMilkRecord object.
     */
    private ParsedMilkRecord parseRecord(CSVRecord csvRecord) throws Exception {
        ParsedMilkRecord record = new ParsedMilkRecord();
        record.setRowNumber((int) csvRecord.getRecordNumber());

        // Parse collection date (Coll_date)
        String collDateStr = getField(csvRecord, "Coll_date", "Coll_Date");
        record.setCollectionDate(parseDate(collDateStr));

        // Parse Nepali date (Ne_date) - format: 09/07/2082
        String nepaliDateStr = getField(csvRecord, "Ne_date", "ne_date");
        record.setNepaliDate(nepaliDateStr);

        // Extract Nepali month and year from nepali date
        String[] nepaliParts = extractNepaliMonthYear(nepaliDateStr);
        record.setNepaliMonth(nepaliParts[0]);  // Month
        record.setNepaliYear(nepaliParts[1]);   // Year

        // Parse collection time (Coll_time)
        String timeStr = getField(csvRecord, "Coll_time", "coll_time");
        record.setCollectionTime(parseTime(timeStr));

        // Parse member code (Mem_code)
        record.setMemberCode(getField(csvRecord, "Mem_code", "mem_code"));

        // Parse volume in liters (Volume_lt)
        String volumeStr = getField(csvRecord, "Volume_lt", "volume_lt");
        record.setVolumeLiters(parseDouble(volumeStr));

        // Parse fat percentage (Fat_per)
        String fatStr = getField(csvRecord, "Fat_per", "fat_per");
        record.setFatPercentage(parseDouble(fatStr));

        // Parse SNF (Snf)
        String snfStr = getField(csvRecord, "Snf", "snf", "SNF");
        record.setSnf(parseDouble(snfStr));

        // Parse rate (Rate)
        String rateStr = getField(csvRecord, "Rate", "rate");
        record.setRate(parseDouble(rateStr));

        // Parse amount (Amount) - may have "LFS" suffix
        String amountStr = getField(csvRecord, "Amount", "amount");
        record.setAmount(parseAmount(amountStr));

        // Parse remarks (Remark) - optional
        try {
            String remarksStr = getField(csvRecord, "Remark", "remark", "Remarks");
            record.setRemarks(remarksStr);
        } catch (Exception e) {
            record.setRemarks(null);  // Remarks are optional
        }

        return record;
    }

    /**
     * Get field from CSV record with multiple possible column names.
     * Case-insensitive matching for flexibility.
     */
    private String getField(CSVRecord record, String... possibleNames) throws Exception {
        for (String name : possibleNames) {
            try {
                // Try exact match first
                if (record.isMapped(name)) {
                    String value = record.get(name);
                    if (value != null && !value.trim().isEmpty()) {
                        return value.trim();
                    }
                }

                // Try lowercase match
                String lowerName = name.toLowerCase();
                if (record.isMapped(lowerName)) {
                    String value = record.get(lowerName);
                    if (value != null && !value.trim().isEmpty()) {
                        return value.trim();
                    }
                }

                // Try uppercase match
                String upperName = name.toUpperCase();
                if (record.isMapped(upperName)) {
                    String value = record.get(upperName);
                    if (value != null && !value.trim().isEmpty()) {
                        return value.trim();
                    }
                }
            } catch (IllegalArgumentException e) {
                // Column not found, try next name
            }
        }
        throw new Exception("Required field not found. Tried: " + String.join(", ", possibleNames));
    }

    /**
     * Parse collection date from string (yyyy-MM-dd format).
     */
    /**
     * Parse date from string (Coll_date - supports multiple formats).
     */
    private LocalDate parseDate(String dateStr) throws Exception {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new Exception("Date is empty");
        }

        // Try yyyy-MM-dd format first (e.g., 2025-10-13)
        try {
            return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException e1) {
            // Try MM/dd/yyyy format (e.g., 10/26/2025)
            try {
                return LocalDate.parse(dateStr, COLL_DATE_FORMAT);
            } catch (DateTimeParseException e2) {
                throw new Exception("Invalid date format: " + dateStr + " (expected yyyy-MM-dd or MM/dd/yyyy)");
            }
        }
    }

    /**
     * Parse time from string (HH:mm format).
     */
    private LocalTime parseTime(String timeStr) throws Exception {
        if (timeStr == null || timeStr.trim().isEmpty()) {
            throw new Exception("Time is empty");
        }

        try {
            return LocalTime.parse(timeStr, TIME_FORMAT);
        } catch (DateTimeParseException e) {
            throw new Exception("Invalid time format: " + timeStr);
        }
    }

    /**
     * Parse double value from string.
     */
    private Double parseDouble(String value) throws Exception {
        if (value == null || value.trim().isEmpty()) {
            throw new Exception("Numeric value is empty");
        }

        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            throw new Exception("Invalid numeric value: " + value);
        }
    }

    /**
     * Parse amount from string.
     * Amount column contains only numbers (no LFS suffix - that's in Remarks).
     */
    private Double parseAmount(String amountStr) throws Exception {
        if (amountStr == null || amountStr.trim().isEmpty()) {
            throw new Exception("Amount is empty");
        }

        return parseDouble(amountStr);
    }

    /**
     * Extract Nepali month and year from Nepali date string.
     * Format: 09/07/2082 → month=07, year=2082
     */
    private String[] extractNepaliMonthYear(String nepaliDate) throws Exception {
        if (nepaliDate == null || nepaliDate.trim().isEmpty()) {
            throw new Exception("Nepali date is empty");
        }

        String[] parts = nepaliDate.split("/");
        if (parts.length != 3) {
            throw new Exception("Invalid Nepali date format: " + nepaliDate);
        }

        String month = parts[1];  // Middle part is month
        String year = parts[2];   // Last part is year

        return new String[]{month, year};
    }

    /**
     * Inner class to hold parsed CSV record data.
     */
    public static class ParsedMilkRecord {
        private int rowNumber;
        private LocalDate collectionDate;
        private String nepaliDate;
        private String nepaliMonth;
        private String nepaliYear;
        private LocalTime collectionTime;
        private String memberCode;
        private Double volumeLiters;
        private Double fatPercentage;
        private Double snf;
        private Double rate;
        private Double amount;
        private String remarks;
        private String error;  // For error tracking

        // Getters and setters
        public int getRowNumber() { return rowNumber; }
        public void setRowNumber(int rowNumber) { this.rowNumber = rowNumber; }

        public LocalDate getCollectionDate() { return collectionDate; }
        public void setCollectionDate(LocalDate collectionDate) { this.collectionDate = collectionDate; }

        public String getNepaliDate() { return nepaliDate; }
        public void setNepaliDate(String nepaliDate) { this.nepaliDate = nepaliDate; }

        public String getNepaliMonth() { return nepaliMonth; }
        public void setNepaliMonth(String nepaliMonth) { this.nepaliMonth = nepaliMonth; }

        public String getNepaliYear() { return nepaliYear; }
        public void setNepaliYear(String nepaliYear) { this.nepaliYear = nepaliYear; }

        public LocalTime getCollectionTime() { return collectionTime; }
        public void setCollectionTime(LocalTime collectionTime) { this.collectionTime = collectionTime; }

        public String getMemberCode() { return memberCode; }
        public void setMemberCode(String memberCode) { this.memberCode = memberCode; }

        public Double getVolumeLiters() { return volumeLiters; }
        public void setVolumeLiters(Double volumeLiters) { this.volumeLiters = volumeLiters; }

        public Double getFatPercentage() { return fatPercentage; }
        public void setFatPercentage(Double fatPercentage) { this.fatPercentage = fatPercentage; }

        public Double getSnf() { return snf; }
        public void setSnf(Double snf) { this.snf = snf; }

        public Double getRate() { return rate; }
        public void setRate(Double rate) { this.rate = rate; }

        public Double getAmount() { return amount; }
        public void setAmount(Double amount) { this.amount = amount; }

        public String getRemarks() { return remarks; }
        public void setRemarks(String remarks) { this.remarks = remarks; }

        public String getError() { return error; }
        public void setError(String error) { this.error = error; }

        public boolean hasError() {
            return error != null && !error.isEmpty();
        }
    }
}