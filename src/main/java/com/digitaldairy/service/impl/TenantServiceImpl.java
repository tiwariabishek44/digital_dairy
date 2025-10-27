package com.digitaldairy.service.impl;

/**
 * TenantServiceImpl: Implements tenant operations (dairy center management).
 *
 * Business Logic:
 * - Validates unique dairy center name (case-insensitive)
 * - Creates and persists DairyCenter entities
 * - Returns paginated results for listing
 *
 * Note: Returns raw entities (Page<DairyCenter>) - controller handles DTO mapping.
 * Multi-tenancy not needed here (DairyCenter is the root tenant entity).
 */

import com.digitaldairy.dto.request.DairyOnboardRequest;
import com.digitaldairy.dto.response.DairyOnboardResponse;
import com.digitaldairy.exception.DuplicateDairyCenterException;
import com.digitaldairy.model.DairyCenter;
import com.digitaldairy.repository.DairyCenterRepository;
import com.digitaldairy.service.TenantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TenantServiceImpl implements TenantService {

    private final DairyCenterRepository dairyCenterRepository;

    @Override
    public DairyOnboardResponse onboardDairyCenter(DairyOnboardRequest request) {
        log.debug("Onboarding dairy center: name='{}', location='{}'",
                request.getName(), request.getLocation());

        // Check for duplicate name (case-insensitive)
        if (dairyCenterRepository.findByNameIgnoreCase(request.getName()).isPresent()) {
            log.warn("Duplicate dairy center name attempted: '{}'", request.getName());
            throw new DuplicateDairyCenterException(request.getName());
        }

        // Create entity from request
        DairyCenter dairyCenter = new DairyCenter(
                request.getName(),
                request.getLocation(),
                request.getContact()
        );
        // Note: isActive defaults to true in entity

        // Save and flush to get generated ID
        DairyCenter saved = dairyCenterRepository.saveAndFlush(dairyCenter);

        log.info("Dairy center onboarded successfully: id={}, name='{}'",
                saved.getId(), saved.getName());

        // Map entity to response DTO
        return new DairyOnboardResponse(
                saved.getId(),
                saved.getName(),
                saved.getLocation(),
                saved.getContact(),
                "Dairy center '" + saved.getName() + "' onboarded successfully."
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DairyCenter> getAllDairyCenters(Pageable pageable) {
        log.debug("Fetching all dairy centers: page={}, size={}",
                pageable.getPageNumber(), pageable.getPageSize());

        // Fetch paginated dairy centers
        // Returns empty page if no results - NOT an error (controller returns 200 OK)
        Page<DairyCenter> centers = dairyCenterRepository.findAll(pageable);

        log.debug("Found {} dairy centers (total: {}, page: {} of {})",
                centers.getNumberOfElements(),
                centers.getTotalElements(),
                centers.getNumber() + 1,
                centers.getTotalPages());

        return centers;
    }
}