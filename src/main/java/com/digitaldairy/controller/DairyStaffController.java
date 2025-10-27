package com.digitaldairy.controller;

/**
 * DairyStaffController: REST controller for dairy staff operations.
 * Extends BaseController for consistent generic responses.
 *
 * Endpoints:
 * 1. POST /api/staff/create - Create staff account (no OTP, direct via Postman)
 * 2. POST /api/staff/login - Staff login (phone + password)
 * 3. GET /api/staff/{id} - Get staff details by ID
 * 4. GET /api/staff/dairy/{dairyCenterId} - Get all staff for a dairy center
 *
 * Security:
 * - Create account: Admin only (for MVP)
 * - Login: Public (no auth required)
 * - Get staff: Staff or Admin role required
 */

import com.digitaldairy.dto.request.DairyStaffRequest;
import com.digitaldairy.dto.request.LoginRequest;
import com.digitaldairy.dto.response.ApiResponse;
import com.digitaldairy.dto.response.DairyStaffResponse;
import com.digitaldairy.dto.response.LoginResponse;
import com.digitaldairy.dto.response.PagedResponse;
import com.digitaldairy.service.DairyStaffService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/staff")
@RequiredArgsConstructor
@Tag(name = "Dairy Staff Management", description = "APIs for managing dairy staff accounts and authentication")
public class DairyStaffController extends BaseController {

    private final DairyStaffService dairyStaffService;


    /**
     * Create new dairy staff account.
     * No OTP - direct creation via Postman by admin.
     *
     * Request body:
     * {
     *   "name": "Ram Bahadur Sharma",
     *   "phone": "9841234567",
     *   "password": "Staff@123",
     *   "dairyCenterId": 1
     * }
     */
    @PostMapping("/create")
     @Operation(
            summary = "Create staff account",
            description = "Create new dairy staff member account (no OTP required, direct creation)"
    )
    public ResponseEntity<ApiResponse<DairyStaffResponse>> createStaffAccount(
            @Valid @RequestBody DairyStaffRequest request) {

        log.info("Creating staff account: phone={}, dairyCenterId={}",
                request.getPhone(), request.getDairyCenterId());

        DairyStaffResponse response = dairyStaffService.createStaffAccount(request);

        log.info("Staff account created: id={}, phone={}, dairy='{}'",
                response.getId(), response.getPhone(), response.getDairyCenterName());

        // ✅ Generic created() - returns 201
        return created(response, "Staff account created successfully");
    }


    /**
     * Staff login.
     * Uses phone + password (no dairyGivenId like farmers).
     *
     * Request body:
     * {
     *   "phone": "9841234567",
     *   "password": "Staff@123"
     * }
     */
    @PostMapping("/login")
    @Operation(
            summary = "Staff login",
            description = "Authenticate dairy staff member with phone and password"
    )
    public ResponseEntity<ApiResponse<LoginResponse>> staffLogin(
            @Valid @RequestBody LoginRequest request) {

        log.info("Staff login attempt: phone={}", request.getPhone());

        LoginResponse response = dairyStaffService.staffLogin(request);

        log.info("Staff login successful: phone={}, dairy='{}'",
                request.getPhone(), response.getDairyName());

        // ✅ Generic ok() - returns 200
        return ok(response, "Login successful");
    }


    /**
     * Get staff details by ID.
     * Returns staff profile information.
     */
    @GetMapping("/{id}")
     @Operation(
            summary = "Get staff by ID",
            description = "Retrieve dairy staff member details by ID"
    )
    public ResponseEntity<ApiResponse<DairyStaffResponse>> getStaffById(
            @PathVariable Long id) {

        log.info("Fetching staff details: id={}", id);

        DairyStaffResponse response = dairyStaffService.getStaffById(id);

        log.debug("Staff details retrieved: id={}, phone={}",
                response.getId(), response.getPhone());

        // ✅ Generic ok() - returns 200
        return ok(response, "Staff details retrieved successfully");
    }


    /**
     * Get all staff for a specific dairy center.
     * Returns paginated list.
     */
    @GetMapping("/dairy/{dairyCenterId}")
    @PreAuthorize("hasRole('DAIRY_STAFF') or hasRole('ADMIN')")
    @Operation(
            summary = "Get staff by dairy center",
            description = "Retrieve all staff members for a specific dairy center"
    )
    public ResponseEntity<ApiResponse<PagedResponse<DairyStaffResponse>>> getAllStaffByDairy(
            @PathVariable Long dairyCenterId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Fetching staff for dairy: dairyCenterId={}, page={}, size={}",
                dairyCenterId, page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<DairyStaffResponse> staffPage = dairyStaffService.getAllStaffByDairy(dairyCenterId, pageable);

        log.debug("Retrieved {} staff members for dairy center {}",
                staffPage.getNumberOfElements(), dairyCenterId);

        // ✅ Generic okPaged() - handles empty automatically, returns 200
        return okPaged(staffPage, "Staff members retrieved");
    }
}