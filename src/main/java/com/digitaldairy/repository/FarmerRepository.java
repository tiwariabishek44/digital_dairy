package com.digitaldairy.repository;

/**
 * FarmerRepository: Spring Data JPA repo for Farmer (separate table).
 * Custom queries for login (phone + dairyGivenId + tenant), uniqueness, and broadcasts.
 * Tenant isolation auto-applied via TenantConfig AOP (WHERE dairy_center_id = ?).
 */

import com.digitaldairy.model.Farmer;
import com.digitaldairy.model.DairyCenter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FarmerRepository extends JpaRepository<Farmer, Long> {

    // For login: Composite phone + dairyGivenId + tenant
    Optional<Farmer> findByPhoneAndDairyGivenIdAndDairyCenterId(String phone, String dairyGivenId, Long dairyCenterId);

    // For reg validation: Check unique phone + dairyGivenId per tenant
    boolean existsByPhoneAndDairyGivenIdAndDairyCenterId(String phone, String dairyGivenId, Long dairyCenterId);

    // For quick lookup by dairyGivenId + tenant (e.g., milk records)
    Optional<Farmer> findByDairyGivenIdAndDairyCenterId(String dairyGivenId, Long dairyCenterId);

    // List farmers for broadcasts (by tenant)
    List<Farmer> findByDairyCenterId(Long dairyCenterId);

    // Update FCM token on login
    @Query("UPDATE Farmer f SET f.fcmToken = :fcmToken WHERE f.phone = :phone AND f.dairyCenterId = :dairyCenterId")
    void updateFcmToken(@Param("phone") String phone, @Param("fcmToken") String fcmToken, @Param("dairyCenterId") Long dairyCenterId);

    // Count farmers per dairy
    long countByDairyCenterId(Long dairyCenterId);
}