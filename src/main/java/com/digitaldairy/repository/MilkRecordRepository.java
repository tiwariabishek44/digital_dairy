package com.digitaldairy.repository;

/**
 * MilkRecordRepository: Spring Data JPA repository for MilkRecord.
 * Query methods for finding records by farmer, Nepali month/year, dairy center.
 * Tenant isolation auto-applied via TenantConfig AOP.
 */

import com.digitaldairy.model.MilkRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface MilkRecordRepository extends JpaRepository<MilkRecord, Long> {

    // Find records by member code and dairy center (all records for a farmer)
    List<MilkRecord> findByMemberCodeAndDairyCenterId(String memberCode, Long dairyCenterId);

    // Find records by member code, dairy center, and Nepali month/year (for mobile app)
    List<MilkRecord> findByMemberCodeAndDairyCenterIdAndNepaliMonthAndNepaliYear(
            String memberCode, Long dairyCenterId, String nepaliMonth, String nepaliYear);

    // Find records by dairy center and Nepali month/year (for staff reports)
    List<MilkRecord> findByDairyCenterIdAndNepaliMonthAndNepaliYear(
            Long dairyCenterId, String nepaliMonth, String nepaliYear);

    // Find records by dairy center and date (for daily reports)
    List<MilkRecord> findByDairyCenterIdAndCollectionDate(Long dairyCenterId, LocalDate collectionDate);

    // Check if record exists (for duplicate prevention during CSV upload)
    boolean existsByMemberCodeAndCollectionDateAndCollectionTimeAndDairyCenterId(
            String memberCode, LocalDate collectionDate, LocalTime collectionTime, Long dairyCenterId);
}