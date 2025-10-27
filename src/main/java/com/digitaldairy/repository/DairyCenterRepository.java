package com.digitaldairy.repository;

/**
 * DairyCenterRepository: Spring Data JPA repo for DairyCenter (tenants).
 * Basic CRUD + custom queries for active centers, by ID/name.
 * No tenant filter needed (this is the tenant root).
 */

import com.digitaldairy.model.DairyCenter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DairyCenterRepository extends JpaRepository<DairyCenter, Long> {

    // Find by name for onboarding (case-insensitive)
    Optional<DairyCenter> findByNameIgnoreCase(String name);

    // List active centers (for admin dashboards)
    List<DairyCenter> findByIsActiveTrue();

    // Count total centers
    long countByIsActiveTrue();
}