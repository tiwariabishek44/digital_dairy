package com.digitaldairy.repository;

/**
 * DairyStaffRepository: Spring Data JPA repo for DairyStaff (separate table).
 * Custom queries for login (phone + tenant), uniqueness.
 * Tenant isolation auto-applied via TenantConfig AOP.
 */

import com.digitaldairy.model.DairyStaff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DairyStaffRepository extends JpaRepository<DairyStaff, Long> {

    // For login: Phone + tenant
    Optional<DairyStaff> findByPhoneAndDairyCenterId(String phone, Long dairyCenterId);

    // For onboarding validation: Unique phone per tenant
    boolean existsByPhoneAndDairyCenterId(String phone, Long dairyCenterId);

    // Count staff per dairy
    long countByDairyCenterId(Long dairyCenterId);
}