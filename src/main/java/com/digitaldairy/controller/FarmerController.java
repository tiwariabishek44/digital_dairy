package com.digitaldairy.controller;

/**
 * FarmerController: REST controller for farmer operations.
 */

import com.digitaldairy.dto.request.FarmerRegistrationRequest;
import com.digitaldairy.dto.request.LoginRequest;
import com.digitaldairy.dto.response.ApiResponse;
import com.digitaldairy.dto.response.FarmerRegistrationResponse;
import com.digitaldairy.dto.response.LoginResponse;
import com.digitaldairy.service.FarmerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/farmer")
@RequiredArgsConstructor
@Tag(name = "Farmer Management", description = "APIs for farmer registration and authentication")
public class FarmerController extends BaseController {

    private final FarmerService farmerService;

    @PostMapping("/register")
    @Operation(summary = "Register farmer", description = "Farmer self-registration")
    public ResponseEntity<ApiResponse<FarmerRegistrationResponse>> registerFarmer(
            @Valid @RequestBody FarmerRegistrationRequest request) {

        log.info("Farmer registration: phone={}, dairyGivenId={}",
                request.getPhone(), request.getDairyGivenId());

        FarmerRegistrationResponse response = farmerService.registerFarmer(request);

        log.info("Farmer registered: id={}, phone={}, dairyGivenId={}",
                response.getId(), response.getPhone(), response.getDairyGivenId());

        return created(response, "Farmer registered successfully");
    }

    @PostMapping("/login")
    @Operation(summary = "Farmer login", description = "Authenticate farmer with phone, dairyGivenId, and password")
    public ResponseEntity<ApiResponse<LoginResponse>> farmerLogin(
            @Valid @RequestBody LoginRequest request) {

        log.info("Farmer login: phone={}, dairyGivenId={}",
                request.getPhone(), request.getDairyGivenId());

        LoginResponse response = farmerService.farmerLogin(request);

        log.info("Farmer login successful: phone={}, dairyGivenId={}",
                request.getPhone(), request.getDairyGivenId());

        return ok(response, "Login successful");
    }
}