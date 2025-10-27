package com.digitaldairy.model;

/**
 * Farmer: Separate JPA entity for farmers (no inheritance—clean table for type-specific fields).
 * Fields: name, phone (login key), password (hashed), dairyGivenId (composite with phone for uniqueness),
 * fcmToken (for pushes), dairyCenterId (FK to DairyCenter for tenancy).
 * Auditing for timestamps. Self-reg via service.
 */

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "farmers")
@Data
@EntityListeners(AuditingEntityListener.class)
public class Farmer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = false)  // Unique composite with dairyGivenId via service
    private String phone;

    @Column(nullable = false)
    private String password;  // BCrypt hashed

    @Column(name = "dairy_given_id", nullable = false)  // Dairy-provided farmer number, e.g., "F456"
    private String dairyGivenId;

    @Column(name = "fcm_token")
    private String fcmToken;  // For FCM pushes (updated on login)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dairy_center_id", nullable = false)
    private DairyCenter dairyCenter;  // FK to DairyCenter (tenant)

    @Column(name = "dairy_center_id", insertable = false, updatable = false)  // For queries
    private Long dairyCenterId;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Default constructor for JPA
    public Farmer() {}

    // Convenience constructor for reg
    public Farmer(String name, String phone, String password, String dairyGivenId, DairyCenter dairyCenter) {
        this.name = name;
        this.phone = phone;
        this.password = password;
        this.dairyGivenId = dairyGivenId;
        this.dairyCenter = dairyCenter;
    }
}