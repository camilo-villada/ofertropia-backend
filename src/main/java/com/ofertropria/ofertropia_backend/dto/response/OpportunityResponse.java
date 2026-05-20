package com.ofertropria.ofertropia_backend.dto.response;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OpportunityResponse {

    private UUID id;
    private String title;
    private String price;
    private String currency;
    private String originalUrl;
    private String description;
    private List<String> images;
    private Map<String, Object> technicalDetails;
    private Boolean isActive;
    private Map<String, Object> analysis;
    private LocalDateTime detectedAt;
    private LocalDateTime updatedAt;
}
