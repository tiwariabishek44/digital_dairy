package com.digitaldairy.model;

/**
 * DairyCenter: JPA entity for dairy centers (tenants in multi-tenancy setup).
 * Root for isolation: All farmers/staff link via dairyCenterId FK.
 * Fields: name (e.g., "Chitwan Dairy"), location, contact for onboarding/admin.
 * Auditing for created/updated timestamps.
 */

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "dairy_centers")
@Data
@EntityListeners(AuditingEntityListener.class)
public class DairyCenter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;  // e.g., "Chitwan Dairy Center"

    @Column(nullable = false)
    private String location;  // e.g., "Bharatpur, Chitwan"

    @Column
    private String contact;  // Dairy phone/email

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // Default constructor for JPA
    public DairyCenter() {}

    // Convenience constructor for onboarding
    public DairyCenter(String name, String location, String contact) {
        this.name = name;
        this.location = location;
        this.contact = contact;
    }
}