package com.digitaldairy.model;

/**
 * MilkRecord: JPA entity for milk collection records.
 * Stores daily milk collection data from CSV uploads.
 * Uses memberCode (dairyGivenId) to link to Farmer - no direct FK needed.
 */

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "milk_records")
@Data
@EntityListeners(AuditingEntityListener.class)
public class MilkRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "collection_date", nullable = false)
    private LocalDate collectionDate;

    @Column(name = "nepali_date")
    private String nepaliDate;

    @Column(name = "nepali_month")
    private String nepaliMonth;

    @Column(name = "nepali_year")
    private String nepaliYear;

    @Column(name = "collection_time", nullable = false)
    private LocalTime collectionTime;

    @Column(name = "member_code", nullable = false)
    private String memberCode;  // Maps to Farmer's dairyGivenId

    @Column(name = "volume_liters", nullable = false)
    private Double volumeLiters;

    @Column(name = "fat_percentage", nullable = false)
    private Double fatPercentage;

    @Column(nullable = false)
    private Double snf;

    @Column(nullable = false)
    private Double rate;

    @Column(nullable = false)
    private Double amount;

    @Column(length = 500)
    private String remarks;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dairy_center_id", nullable = false)
    private DairyCenter dairyCenter;

    @Column(name = "dairy_center_id", insertable = false, updatable = false)
    private Long dairyCenterId;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public MilkRecord() {}

    public MilkRecord(LocalDate collectionDate, String nepaliDate, String nepaliMonth,
                      String nepaliYear, LocalTime collectionTime, String memberCode,
                      Double volumeLiters, Double fatPercentage, Double snf, Double rate,
                      Double amount, String remarks, DairyCenter dairyCenter) {
        this.collectionDate = collectionDate;
        this.nepaliDate = nepaliDate;
        this.nepaliMonth = nepaliMonth;
        this.nepaliYear = nepaliYear;
        this.collectionTime = collectionTime;
        this.memberCode = memberCode;
        this.volumeLiters = volumeLiters;
        this.fatPercentage = fatPercentage;
        this.snf = snf;
        this.rate = rate;
        this.amount = amount;
        this.remarks = remarks;
        this.dairyCenter = dairyCenter;
    }
}