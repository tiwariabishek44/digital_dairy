package com.digitaldairy.service;

/**
 * FarmerService: Interface for farmer operations.
 */

import com.digitaldairy.dto.request.FarmerRegistrationRequest;
import com.digitaldairy.dto.request.LoginRequest;
import com.digitaldairy.dto.response.FarmerRegistrationResponse;
import com.digitaldairy.dto.response.LoginResponse;

public interface FarmerService {

    FarmerRegistrationResponse registerFarmer(FarmerRegistrationRequest request);

    LoginResponse farmerLogin(LoginRequest request);
}