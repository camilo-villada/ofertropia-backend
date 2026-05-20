package com.ofertropria.ofertropia_backend.mapper;

import com.ofertropria.ofertropia_backend.dto.response.OpportunityResponse;
import com.ofertropria.ofertropia_backend.entity.Opportunity;

public class OpportunityMapper {
    
    public static OpportunityResponse toResponse(Opportunity opportunity) {
        
        return OpportunityResponse.builder()
                .id(opportunity.getId())
                .title(opportunity.getTitle())
                .price(opportunity.getPrice() != null ? opportunity.getPrice().toString() : null)
                .currency(opportunity.getCurrency())
                .originalUrl(opportunity.getOriginalUrl())
                .description(opportunity.getDescription())
                .images(opportunity.getImages())
                .technicalDetails(opportunity.getTechnicalDetails())
                .isActive(opportunity.getIsActive())
                .analysis(opportunity.getAnalysis())
                .detectedAt(opportunity.getDetectedAt())
                .updatedAt(opportunity.getUpdatedAt())
                .build();
    }
}
