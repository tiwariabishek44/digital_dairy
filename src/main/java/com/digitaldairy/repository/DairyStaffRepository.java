package com.digitaldairy.repository;

/**
 * DairyStaffRepository: Spring Data JPA repo for DairyStaff (separate table).
 * Custom queries for login (phone + tenant), uniqueness, and paginated retrieval.
 * Tenant isolation auto-applied via TenantConfig AOP.
 *
 * UPDATED: Added findByDairyCenterId with Pageable for efficient pagination.
 */

import com.digitaldairy.model.DairyStaff;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    // ✅ NEW: Paginated query for staff by dairy center
    // This replaces the manual filtering in service layer
    // Spring Data JPA automatically implements this method
    Page<DairyStaff> findByDairyCenterId(Long dairyCenterId, Pageable pageable);
}