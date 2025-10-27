package com.digitaldairy.controller;

/**
 * MilkRecordController: REST controller for milk record operations.
 * Handles CSV upload (staff) and record queries (staff/farmers).
 */

import com.digitaldairy.dto.response.ApiResponse;
import com.digitaldairy.dto.response.CsvUploadResponse;
import com.digitaldairy.dto.response.MilkRecordResponse;
import com.digitaldairy.service.MilkRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/milk")
@RequiredArgsConstructor
@Tag(name = "Milk Record Management", description = "APIs for milk record CSV upload and queries")
public class MilkRecordController extends BaseController {

    private final MilkRecordService milkRecordService;

    /**
     * Upload CSV file with milk records.
     * Staff only - requires DAIRY_STAFF role.
     *
     * Request:
     * - file: CSV file (multipart/form-data)
     * - dairyCenterId: Dairy center ID (from form data or JWT)
     */
    @PostMapping("/upload")

    @Operation(
            summary = "Upload milk records CSV",
            description = "Upload and process CSV file from milk analyzer (staff only)"
    )
    public ResponseEntity<ApiResponse<CsvUploadResponse>> uploadCsv(
            @RequestParam("file") MultipartFile file,
            @RequestParam("dairyCenterId") Long dairyCenterId) {

        log.info("CSV upload request: dairyCenterId={}, filename={}, size={}",
                dairyCenterId, file.getOriginalFilename(), file.getSize());

        CsvUploadResponse response = milkRecordService.uploadCsv(file, dairyCenterId);

        log.info("CSV upload complete: total={}, success={}, failed={}",
                response.getTotalRecords(), response.getSuccessfulRecords(), response.getFailedRecords());

        // Return 200 even if some records failed (partial success)
        String message = String.format(
                "CSV processed: %d/%d records saved successfully",
                response.getSuccessfulRecords(), response.getTotalRecords()
        );

        return ok(response, message);
    }


    @GetMapping("/test")

    @Operation(summary = "Test staff authentication")
    public ResponseEntity<ApiResponse<String>> testStaffAuth() {
        return ok("Hello work", "Staff authentication successful");
    }
    /**
     * Get farmer's milk records for a specific Nepali month.
     * For mobile app - farmer views monthly data.
     */
    @GetMapping("/farmer/{memberCode}/month")
    @Operation(
            summary = "Get farmer's monthly records",
            description = "Retrieve milk records for a farmer for a specific Nepali month"
    )
    public ResponseEntity<ApiResponse<List<MilkRecordResponse>>> getFarmerMonthlyRecords(
            @PathVariable String memberCode,
            @RequestParam String nepaliMonth,
            @RequestParam String nepaliYear,
            @RequestParam Long dairyCenterId) {

        log.info("Fetching farmer monthly records: memberCode={}, month={}, year={}, dairyCenterId={}",
                memberCode, nepaliMonth, nepaliYear, dairyCenterId);

        List<MilkRecordResponse> records = milkRecordService.getFarmerRecordsByNepaliMonth(
                memberCode, nepaliMonth, nepaliYear, dairyCenterId);

        if (records.isEmpty()) {
            return okEmptyList(String.format(
                    "No records found for farmer %s in %s/%s", memberCode, nepaliMonth, nepaliYear));
        }

        return okList(records, "Monthly records retrieved");
    }

    /**
     * Get all milk records for a dairy center by Nepali month.
     * Staff only - for generating monthly reports.
     */
    @GetMapping("/dairy/month")
    @Operation(
            summary = "Get dairy monthly records",
            description = "Retrieve all milk records for a dairy center for a specific Nepali month (staff only)"
    )
    public ResponseEntity<ApiResponse<List<MilkRecordResponse>>> getDairyMonthlyRecords(
            @RequestParam String nepaliMonth,
            @RequestParam String nepaliYear,
            @RequestParam Long dairyCenterId) {

        log.info("Fetching dairy monthly records: month={}, year={}, dairyCenterId={}",
                nepaliMonth, nepaliYear, dairyCenterId);

        List<MilkRecordResponse> records = milkRecordService.getDairyRecordsByNepaliMonth(
                nepaliMonth, nepaliYear, dairyCenterId);

        if (records.isEmpty()) {
            return okEmptyList(String.format(
                    "No records found for %s/%s", nepaliMonth, nepaliYear));
        }

        return okList(records, "Dairy monthly records retrieved");
    }
}