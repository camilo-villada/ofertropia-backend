package com.ofertropria.ofertropia_backend.service;

import com.ofertropria.ofertropia_backend.DTO.OpportunityRequest;
import com.ofertropria.ofertropia_backend.model.Opportunity;
import com.ofertropria.ofertropia_backend.repository.OpportunityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OpportunityService {

    private final OpportunityRepository opportunityRepository;
    private final AiService aiService; // <-- El cerebro conectado

    @Transactional
    public Opportunity processAndSave(OpportunityRequest request) {
        // 1. Consultamos a Gemini
        String aiResponse = aiService.analyzeOpportunity(
            request.getTitle(), 
            request.getDescription(), 
            request.getPrice().doubleValue()
        );

        // 2. Construimos la entidad incluyendo el análisis
        Opportunity opportunity = Opportunity.builder()
                .title(request.getTitle())
                .price(request.getPrice())
                .originalUrl(request.getOriginalUrl())
                .description(request.getDescription() + " [AI Veredicto: " + aiResponse + "]")
                .images(request.getImages())
                .technicalDetails(request.getTechnicalDetails())
                .subcategoryId(request.getSubcategoryId())
                .locationId(request.getLocationId())
                .build();

        return opportunityRepository.save(opportunity);
    }

    @Transactional(readOnly = true)
    public java.util.List<Opportunity> findAll() {
        return opportunityRepository.findAll();
    }
}