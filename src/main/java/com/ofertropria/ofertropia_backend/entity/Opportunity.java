package com.ofertropria.ofertropia_backend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(
    name = "opportunities",
    indexes = {
        @Index(name = "idx_opportunity_active_detected_at", columnList = "is_active, detected_at"),
        @Index(name = "idx_opportunity_detected_at", columnList = "detected_at")
    }
)
@Getter @Setter 
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Opportunity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NotBlank
    private String title;

    @NotNull
    private BigDecimal price;

    @Builder.Default
    private String currency = "COP";

    @Column(name = "original_url", unique = true, nullable = false)
    @NotBlank
    private String originalUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Hibernate 6 maneja las listas como JSON automáticamente si se lo pedimos
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "images")
    private List<String> images;

    // Esto mapeará a JSONB en Postgres y a JSON/TEXT en H2 de forma transparente
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "technical_details")
    private Map<String, Object> technicalDetails;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> analysis;


    @Column(name = "detected_at")
    @Builder.Default
    private LocalDateTime detectedAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "subcategory_id")
    private Integer subcategoryId;

    @Column(name = "location_id")
    private Integer locationId;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        if (detectedAt == null) {
            detectedAt = now;
        }

        updatedAt = now;

        if (currency == null || currency.isBlank()) {
            currency = "COP";
        }

        if (isActive == null) {
            isActive = true;
        }
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();

        if (currency == null || currency.isBlank()) {
            currency = "COP";
        }
    }
}
