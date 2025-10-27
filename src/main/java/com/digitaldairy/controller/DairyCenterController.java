package com.digitaldairy.controller;

/**
 * DairyCenterController: REST controller for dairy center (tenant) operations.
 * Extends BaseController for consistent generic responses.
 *
 * All response methods use generic helpers:
 * - created() for 201 responses
 * - okPaged() for paginated lists
 * - ok() for single items
 * - okList() for simple lists
 */

import com.digitaldairy.dto.request.DairyOnboardRequest;
import com.digitaldairy.dto.response.ApiResponse;
import com.digitaldairy.dto.response.DairyOnboardResponse;
import com.digitaldairy.dto.response.PagedResponse;
import com.digitaldairy.model.DairyCenter;
import com.digitaldairy.service.TenantService;
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

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api/dairy")
@RequiredArgsConstructor
@Tag(name = "Dairy Center Management", description = "APIs for managing dairy centers (tenants)")
public class DairyCenterController extends BaseController {

    private final TenantService tenantService;


    @PostMapping("/onboard")

    @Operation(summary = "Onboard new dairy center", description = "Create a new dairy center (tenant)")
    public ResponseEntity<ApiResponse<DairyOnboardResponse>> onboardDairyCenter(
            @Valid @RequestBody DairyOnboardRequest request) {

        log.info("Onboarding dairy center: name='{}'", request.getName());

        DairyOnboardResponse response = tenantService.onboardDairyCenter(request);

        log.info("Dairy center onboarded: id={}, name='{}'", response.getId(), response.getName());

        // ✅ Generic created() method - returns 201
        return created(response, "Dairy center onboarded successfully");
    }


    @GetMapping("/centers")

    @Operation(summary = "Get all dairy centers", description = "Retrieve paginated list of dairy centers")
    public ResponseEntity<ApiResponse<PagedResponse<DairyOnboardResponse>>> getAllDairyCenters(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Fetching dairy centers: page={}, size={}", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Page<DairyCenter> centersPage = tenantService.getAllDairyCenters(pageable);

        // Map to DTOs
        List<DairyOnboardResponse> responses = centersPage.getContent().stream()
                .map(center -> new DairyOnboardResponse(
                        center.getId(),
                        center.getName(),
                        center.getLocation(),
                        center.getContact(),
                        null
                ))
                .collect(Collectors.toList());

        // ✅ Generic okPagedMapped() - handles empty automatically, returns 200
        return okPagedMapped(centersPage, responses, "Dairy centers retrieved");
    }



}