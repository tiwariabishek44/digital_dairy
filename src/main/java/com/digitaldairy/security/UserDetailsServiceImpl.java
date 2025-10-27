package com.digitaldairy.security;

/**
 * UserDetailsServiceImpl: Loads UserDetails for auth (farmers: phone + dairyGivenId; staff: phone).
 * Routes to Farmer or DairyStaff repo based on input (dairyGivenId signals farmer).
 * Tenant auto-filtered. Returns generic UserDetails (role as "FARMER" or "DAIRY_STAFF").
 * Password match happens in AuthService; throws UsernameNotFound if no load.
 */

import com.digitaldairy.config.TenantConfig;
import com.digitaldairy.model.Farmer;
import com.digitaldairy.model.DairyStaff;
import com.digitaldairy.repository.FarmerRepository;
import com.digitaldairy.repository.DairyStaffRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private FarmerRepository farmerRepository;

    @Autowired
    private DairyStaffRepository dairyStaffRepository;

    @Override
    public UserDetails loadUserByUsername(String input) throws UsernameNotFoundException {
        Long tenantId = TenantConfig.getCurrentTenant();
        if (input.contains(":")) {
            // Farmer: "phone:dairyGivenId" format
            String[] parts = input.split(":");
            if (parts.length != 2) {
                throw new UsernameNotFoundException("Invalid farmer login: use phone:dairyGivenId");
            }
            String phone = parts[0];
            String dairyGivenId = parts[1];
            Farmer farmer = farmerRepository.findByPhoneAndDairyGivenIdAndDairyCenterId(phone, dairyGivenId, tenantId)
                    .orElseThrow(() -> new UsernameNotFoundException("Farmer not found: " + phone + " ID: " + dairyGivenId));
            return buildUserDetails(farmer.getPhone(), farmer.getPassword(), "FARMER");
        } else {
            // Staff: phone only
            String phone = input;
            DairyStaff staff = dairyStaffRepository.findByPhoneAndDairyCenterId(phone, tenantId)
                    .orElseThrow(() -> new UsernameNotFoundException("Staff not found: " + phone));
            return buildUserDetails(staff.getPhone(), staff.getPassword(), "DAIRY_STAFF");
        }
    }

    private UserDetails buildUserDetails(String username, String password, String role) {
        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + role));
        return org.springframework.security.core.userdetails.User.builder()
                .username(username)
                .password(password)
                .authorities(authorities)
                .build();
    }
}