package com.digitaldairy.service.impl;

/**
 * TenantServiceImpl: Implements tenant operations (dairy center onboarding).
 * Validates request, saves DairyCenter via repo, maps to response.
 * @Transactional for atomicity; throws TenantNotFoundException if dup name.
 * Multi-tenancy not needed here (root entity).
 */

import com.digitaldairy.dto.request.DairyOnboardRequest;
import com.digitaldairy.dto.response.DairyOnboardResponse;
import com.digitaldairy.exception.TenantNotFoundException;
import com.digitaldairy.model.DairyCenter;
import com.digitaldairy.repository.DairyCenterRepository;
import com.digitaldairy.service.TenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TenantServiceImpl implements TenantService {

    @Autowired
    private DairyCenterRepository dairyCenterRepository;

    @Override
    public DairyOnboardResponse onboardDairyCenter(DairyOnboardRequest request) {
        // Check for duplicate name (case-insensitive)
        if (dairyCenterRepository.findByNameIgnoreCase(request.getName()).isPresent()) {
            throw new TenantNotFoundException("Dairy center with name '" + request.getName() + "' already exists.");
        }

        // Map request to entity
        DairyCenter dairyCenter = new DairyCenter(
                request.getName(),
                request.getLocation(),
                request.getContact()
        );

        // Save and flush for ID generation
        DairyCenter saved = dairyCenterRepository.saveAndFlush(dairyCenter);

        // Map to response
        return new DairyOnboardResponse(
                saved.getId(),
                saved.getName(),
                saved.getLocation(),
                saved.getContact(),
                "Dairy center '" + saved.getName() + "' onboarded successfully."
        );
    }
}