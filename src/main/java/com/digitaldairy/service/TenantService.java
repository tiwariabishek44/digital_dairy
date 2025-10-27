package com.digitaldairy.service;

/**
 * TenantService: Interface for dairy center (tenant) operations.
 * Handles onboarding new centers (create DairyCenter from request).
 * Later: List active tenants, update, delete (soft via isActive).
 */

import com.digitaldairy.dto.request.DairyOnboardRequest;
import com.digitaldairy.dto.response.DairyOnboardResponse;

public interface TenantService {

    DairyOnboardResponse onboardDairyCenter(DairyOnboardRequest request);
}