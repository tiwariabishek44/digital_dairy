package com.digitaldairy.service;

/**
 * DairyStaffService: Interface for dairy staff account operations.
 *
 * Methods:
 * 1. createStaffAccount - Create new staff member (no OTP, direct creation)
 * 2. staffLogin - Authenticate staff member (phone + password)
 * 3. getStaffById - Get staff details by ID
 * 4. getAllStaffByDairy - Get all staff for a specific dairy center
 */

import com.digitaldairy.dto.request.DairyStaffRequest;
import com.digitaldairy.dto.request.LoginRequest;
import com.digitaldairy.dto.response.DairyStaffResponse;
import com.digitaldairy.dto.response.LoginResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DairyStaffService {


    DairyStaffResponse createStaffAccount(DairyStaffRequest request);

    LoginResponse staffLogin(LoginRequest request);

    DairyStaffResponse getStaffById(Long staffId);

    Page<DairyStaffResponse> getAllStaffByDairy(Long dairyCenterId, Pageable pageable);
}