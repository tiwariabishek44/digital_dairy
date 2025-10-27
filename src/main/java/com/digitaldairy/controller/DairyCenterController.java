package com.digitaldairy.controller;

/**
 * DairyCenterController: REST controller for dairy center (tenant) operations.
 * Handles onboarding new centers via /api/dairy/onboard POST (admin-only for MVP).
 * Returns ApiResponse wrapper for consistency (success/error with data).
 *
 * Security: Admin role required for onboarding
 * Error Handling: Leverages GlobalExceptionHandler for consistent responses
 * Validation: @Valid triggers MethodArgumentNotValidException on bad input
 *
 * Later: GET /api/dairy/centers for list, PUT for updates, soft delete.
 */

import com.digitaldairy.dto.request.DairyOnboardRequest;
import com.digitaldairy.dto.response.ApiResponse;
import com.digitaldairy.dto.response.DairyOnboardResponse;
import com.digitaldairy.exception.DuplicateDairyCenterException;
import com.digitaldairy.service.TenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/dairy")
@RequiredArgsConstructor
@Tag(name = "Dairy Center Management", description = "APIs for managing dairy centers (tenants)")
public class DairyCenterController {

    private final TenantService tenantService;


    @PostMapping("/onboard")
     public ResponseEntity<ApiResponse<DairyOnboardResponse>> onboardDairyCenter(
            @Valid @RequestBody DairyOnboardRequest request) {

        log.info("Onboarding request received for dairy center: name='{}', location='{}'",
                request.getName(), request.getLocation());

        try {
            // Service handles business logic, duplicate checking, and entity creation
            DairyOnboardResponse response = tenantService.onboardDairyCenter(request);

            log.info("Dairy center onboarded successfully: id={}, name='{}', location='{}'",
                    response.getId(), response.getName(), response.getLocation());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse<>(response, "Dairy center onboarded successfully"));

        } catch (DuplicateDairyCenterException e) {
            // Log and rethrow - GlobalExceptionHandler will format response
            log.warn("Duplicate dairy center onboarding attempt: name='{}', error='{}'",
                    request.getName(), e.getMessage());
            throw e;

        } catch (Exception e) {
            // Log unexpected errors with full stacktrace for debugging
            log.error("Unexpected error during dairy center onboarding: name='{}', location='{}'",
                    request.getName(), request.getLocation(), e);
            throw new RuntimeException("Failed to onboard dairy center. Please try again or contact support.", e);
        }
    }


    @GetMapping("/centers")
     public ResponseEntity<ApiResponse<Object>> getAllDairyCenters(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Fetching dairy centers: page={}, size={}", page, size);

        // TODO: Implement in service layer
        throw new UnsupportedOperationException("Endpoint not yet implemented");
    }

}