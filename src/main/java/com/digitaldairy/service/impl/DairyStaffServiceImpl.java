package com.digitaldairy.service.impl;

/**
 * DairyStaffServiceImpl: Implements dairy staff account operations.
 *
 * Business Logic:
 * - Creates staff accounts with BCrypt password hashing
 * - Validates phone uniqueness per dairy center
 * - Authenticates staff login (phone + password only, no dairyGivenId)
 * - Generates JWT tokens with dairyCenterId for multi-tenancy
 * - Returns staff details with dairy center name
 *
 * UPDATED: Fixed getAllStaffByDairy to use proper repository query instead of manual filtering
 */

import com.digitaldairy.dto.request.DairyStaffRequest;
import com.digitaldairy.dto.request.LoginRequest;
import com.digitaldairy.dto.response.DairyStaffResponse;
import com.digitaldairy.dto.response.LoginResponse;
import com.digitaldairy.exception.TenantNotFoundException;
import com.digitaldairy.model.DairyCenter;
import com.digitaldairy.model.DairyStaff;
import com.digitaldairy.repository.DairyCenterRepository;
import com.digitaldairy.repository.DairyStaffRepository;
import com.digitaldairy.security.JwtUtil;
import com.digitaldairy.service.DairyStaffService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DairyStaffServiceImpl implements DairyStaffService {

    private final DairyStaffRepository dairyStaffRepository;
    private final DairyCenterRepository dairyCenterRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public DairyStaffResponse createStaffAccount(DairyStaffRequest request) {
        log.info("Creating staff account: phone={}, dairyCenterId={}",
                request.getPhone(), request.getDairyCenterId());

        // Validate dairy center exists
        DairyCenter dairyCenter = dairyCenterRepository.findById(request.getDairyCenterId())
                .orElseThrow(() -> {
                    log.error("Dairy center not found: id={}", request.getDairyCenterId());
                    return new TenantNotFoundException(request.getDairyCenterId());
                });

        // Check if phone already exists for this dairy center
        if (dairyStaffRepository.existsByPhoneAndDairyCenterId(
                request.getPhone(), request.getDairyCenterId())) {
            log.warn("Duplicate staff phone for dairy: phone={}, dairyCenterId={}",
                    request.getPhone(), request.getDairyCenterId());
            throw new RuntimeException(
                    "Staff member with phone '" + request.getPhone() +
                            "' already exists for dairy center '" + dairyCenter.getName() + "'");
        }

        // Hash password
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        // Create staff entity
        DairyStaff staff = new DairyStaff(
                request.getName(),
                request.getPhone(),
                hashedPassword,
                dairyCenter
        );

        // Save to database
        DairyStaff savedStaff = dairyStaffRepository.saveAndFlush(staff);

        log.info("Staff account created successfully: id={}, phone={}, dairy='{}'",
                savedStaff.getId(), savedStaff.getPhone(), dairyCenter.getName());

        // Map to response DTO
        return mapToResponse(savedStaff, dairyCenter.getName());
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse staffLogin(LoginRequest request) {
        log.info("Staff login attempt: phone={}", request.getPhone());

        // Validate required fields
        if (request.getPhone() == null || request.getPhone().isBlank()) {
            throw new BadCredentialsException("Phone number is required");
        }
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new BadCredentialsException("Password is required");
        }

        // Find all staff with this phone (could be in multiple dairies)
        // We'll try to match by phone first, then validate password
        List<DairyStaff> staffList = dairyStaffRepository.findAll().stream()
                .filter(s -> s.getPhone().equals(request.getPhone()))
                .collect(Collectors.toList());

        if (staffList.isEmpty()) {
            log.warn("Staff login failed - phone not found: {}", request.getPhone());
            throw new BadCredentialsException("Invalid phone or password");
        }

        // Try to find staff with matching password
        DairyStaff authenticatedStaff = null;
        for (DairyStaff staff : staffList) {
            if (passwordEncoder.matches(request.getPassword(), staff.getPassword())) {
                authenticatedStaff = staff;
                break;
            }
        }

        if (authenticatedStaff == null) {
            log.warn("Staff login failed - invalid password: phone={}", request.getPhone());
            throw new BadCredentialsException("Invalid phone or password");
        }

        // Get dairy center details
        DairyCenter dairyCenter = authenticatedStaff.getDairyCenter();

        // Generate JWT token
        UserDetails userDetails = User.builder()
                .username(authenticatedStaff.getPhone())
                .password(authenticatedStaff.getPassword())
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_DAIRY_STAFF")))
                .build();

        String token = jwtUtil.generateAccessToken(userDetails, dairyCenter.getId());

        log.info("Staff login successful: phone={}, staffId={}, dairy='{}'",
                request.getPhone(), authenticatedStaff.getId(), dairyCenter.getName());

        // Build login response
        return new LoginResponse(
                token,
                authenticatedStaff.getPhone(),
                dairyCenter.getId(),
                dairyCenter.getName(),
                null,  // No dairyGivenId for staff
                "Login successful"
        );
    }


    /**
     * Helper method to map DairyStaff entity to response DTO.
     * ✅ FIXED: Gets dairyCenterId from the relationship to ensure it's never null
     */
    private DairyStaffResponse mapToResponse(DairyStaff staff, String dairyCenterName) {
        return new DairyStaffResponse(
                staff.getId(),
                staff.getName(),
                staff.getPhone(),
                staff.getDairyCenter().getId(),  // ✅ Get from relationship, not direct field
                dairyCenterName,
                staff.getCreatedAt(),
                staff.getUpdatedAt()
        );
    }
}