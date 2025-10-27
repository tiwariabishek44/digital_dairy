package com.digitaldairy.service;

/**
 * TenantService: Interface for dairy center (tenant) operations.
 *
 * Methods:
 * 1. onboardDairyCenter - Create new dairy center
 * 2. getAllDairyCenters - Get paginated list of all dairy centers
 */

import com.digitaldairy.dto.request.DairyOnboardRequest;
import com.digitaldairy.dto.response.DairyOnboardResponse;
import com.digitaldairy.model.DairyCenter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface TenantService {


    DairyOnboardResponse onboardDairyCenter(DairyOnboardRequest request);

    Page<DairyCenter> getAllDairyCenters(Pageable pageable);
}