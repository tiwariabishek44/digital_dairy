package com.digitaldairy.model;

/**
 * DairyStaff: Separate JPA entity for dairy center staff/admins (no farmer-specific fields).
 * Fields: name, phone (login key), password (hashed), dairyCenterId (FK to DairyCenter for tenancy).
 * Auditing for timestamps. Manual creation via Postman/DB.
 */

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "dairy_staff")
@Data
@EntityListeners(AuditingEntityListener.class)
public class DairyStaff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = false)  // Unique per tenant via service
    private String phone;

    @Column(nullable = false)
    private String password;  // BCrypt hashed

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dairy_center_id", nullable = false)
    private DairyCenter dairyCenter;  // FK to DairyCenter (tenant)

    @Column(name = "dairy_center_id", insertable = false, updatable = false)
    private Long dairyCenterId;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Default constructor for JPA
    public DairyStaff() {}

    // Convenience constructor for manual onboarding
    public DairyStaff(String name, String phone, String password, DairyCenter dairyCenter) {
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.dairyCenter = dairyCenter;
    }
}