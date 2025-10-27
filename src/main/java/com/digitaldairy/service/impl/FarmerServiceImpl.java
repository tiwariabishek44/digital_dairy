package com.digitaldairy.service.impl;

/**
 * FarmerServiceImpl: Implements farmer registration and login.
 */

import com.digitaldairy.dto.request.FarmerRegistrationRequest;
import com.digitaldairy.dto.request.LoginRequest;
import com.digitaldairy.dto.response.FarmerRegistrationResponse;
import com.digitaldairy.dto.response.LoginResponse;
import com.digitaldairy.exception.TenantNotFoundException;
import com.digitaldairy.model.DairyCenter;
import com.digitaldairy.model.Farmer;
import com.digitaldairy.repository.DairyCenterRepository;
import com.digitaldairy.repository.FarmerRepository;
import com.digitaldairy.security.JwtUtil;
import com.digitaldairy.service.FarmerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class FarmerServiceImpl implements FarmerService {

    private final FarmerRepository farmerRepository;
    private final DairyCenterRepository dairyCenterRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public FarmerRegistrationResponse registerFarmer(FarmerRegistrationRequest request) {
        log.info("Registering farmer: phone={}, dairyGivenId={}, dairyCenterId={}",
                request.getPhone(), request.getDairyGivenId(), request.getDairyCenterId());

        // Validate dairy center exists
        DairyCenter dairyCenter = dairyCenterRepository.findById(request.getDairyCenterId())
                .orElseThrow(() -> new TenantNotFoundException(request.getDairyCenterId()));

        // Check if farmer already exists (phone + dairyGivenId + dairyCenterId composite)
        if (farmerRepository.existsByPhoneAndDairyGivenIdAndDairyCenterId(
                request.getPhone(), request.getDairyGivenId(), request.getDairyCenterId())) {
            throw new RuntimeException(
                    "Farmer with phone '" + request.getPhone() +
                            "' and ID '" + request.getDairyGivenId() +
                            "' already exists for dairy '" + dairyCenter.getName() + "'");
        }

        // Hash password
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // Create farmer entity
        Farmer farmer = new Farmer(
                request.getName(),
                request.getPhone(),
                hashedPassword,
                request.getDairyGivenId(),
                dairyCenter
        );

        // Save to database
        Farmer savedFarmer = farmerRepository.saveAndFlush(farmer);

        log.info("Farmer registered: id={}, phone={}, dairyGivenId={}, dairy='{}'",
                savedFarmer.getId(), savedFarmer.getPhone(),
                savedFarmer.getDairyGivenId(), dairyCenter.getName());

        return new FarmerRegistrationResponse(
                savedFarmer.getId(),
                savedFarmer.getName(),
                savedFarmer.getPhone(),
                savedFarmer.getDairyGivenId(),
                dairyCenter.getId(),
                dairyCenter.getName(),
                savedFarmer.getCreatedAt()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse farmerLogin(LoginRequest request) {
        log.info("Farmer login attempt: phone={}, dairyGivenId={}",
                request.getPhone(), request.getDairyGivenId());

        // Validate required fields
        if (request.getPhone() == null || request.getPhone().isBlank()) {
            throw new BadCredentialsException("Phone number is required");
        }
        if (request.getDairyGivenId() == null || request.getDairyGivenId().isBlank()) {
            throw new BadCredentialsException("Dairy Given ID is required");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new BadCredentialsException("Password is required");
        }

        // Find farmer by phone + dairyGivenId across all dairies
        // Then match password
        Farmer farmer = farmerRepository.findAll().stream()
                .filter(f -> f.getPhone().equals(request.getPhone()) &&
                        f.getDairyGivenId().equals(request.getDairyGivenId()))
                .findFirst()
                .orElseThrow(() -> {
                    log.warn("Farmer login failed - not found: phone={}, dairyGivenId={}",
                            request.getPhone(), request.getDairyGivenId());
                    return new BadCredentialsException("Invalid phone, ID, or password");
                });

        // Validate password
        if (!passwordEncoder.matches(request.getPassword(), farmer.getPassword())) {
            log.warn("Farmer login failed - invalid password: phone={}, dairyGivenId={}",
                    request.getPhone(), request.getDairyGivenId());
            throw new BadCredentialsException("Invalid phone, ID, or password");
        }

        // Get dairy center details
        DairyCenter dairyCenter = farmer.getDairyCenter();

        // Generate JWT token
        UserDetails userDetails = User.builder()
                .username(farmer.getPhone())
                .password(farmer.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_FARMER")))
                .build();

        String token = jwtUtil.generateAccessToken(userDetails, dairyCenter.getId());

        log.info("Farmer login successful: phone={}, dairyGivenId={}, farmerId={}, dairy='{}'",
                request.getPhone(), request.getDairyGivenId(),
                farmer.getId(), dairyCenter.getName());

        return new LoginResponse(
                token,
                farmer.getPhone(),
                dairyCenter.getId(),
                dairyCenter.getName(),
                farmer.getDairyGivenId(),
                "Login successful"
        );
    }
}