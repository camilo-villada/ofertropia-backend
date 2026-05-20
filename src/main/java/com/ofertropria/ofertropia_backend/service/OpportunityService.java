package com.ofertropria.ofertropia_backend.service;

import com.ofertropria.ofertropia_backend.dto.request.OpportunityRequest;
import com.ofertropria.ofertropia_backend.dto.response.IngestionItemResponse;
import com.ofertropria.ofertropia_backend.dto.response.IngestionResponse;
import com.ofertropria.ofertropia_backend.entity.Opportunity;
import com.ofertropria.ofertropia_backend.repository.OpportunityRepository;
import com.ofertropria.ofertropia_backend.service.scoring.InvestmentScoringService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OpportunityService {

    private final OpportunityRepository opportunityRepository;
    private final InvestmentScoringService scoringService;

    @Transactional
    public Opportunity processAndSave(OpportunityRequest request) {
        return upsertOpportunity(request).opportunity();
    }

    @Transactional
    public IngestionResponse ingest(List<OpportunityRequest> requests) {
        List<IngestionItemResponse> items = new ArrayList<>();
        int created = 0;
        int updated = 0;

        for (OpportunityRequest request : requests) {
            UpsertResult result = upsertOpportunity(request);
            Map<String, Object> analysis = result.opportunity().getAnalysis();
            String status = result.created() ? "created" : "updated";

            if (result.created()) {
                created++;
            } else {
                updated++;
            }

            items.add(IngestionItemResponse.builder()
                    .id(result.opportunity().getId())
                    .originalUrl(result.opportunity().getOriginalUrl())
                    .status(status)
                    .investmentScore(readInteger(analysis.get("investmentScore")))
                    .bargain(Boolean.TRUE.equals(analysis.get("isBargain")))
                    .build());
        }

        return IngestionResponse.builder()
                .received(requests.size())
                .created(created)
                .updated(updated)
                .items(items)
                .build();
    }

    @Transactional(readOnly = true)
    public java.util.List<Opportunity> findAll() {
        return opportunityRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Opportunity> findById(UUID id) {
        return opportunityRepository.findById(id);
    }

    @Transactional(readOnly = true)
public java.util.List<Opportunity> findTopBargains() {

    return opportunityRepository.findByIsActiveTrue()
            .stream()

            // SOLO gangas
            .filter(opportunity -> {
                Map<String, Object> analysis = opportunity.getAnalysis();

                if (analysis == null) {
                    return false;
                }

                Object isBargain =
                        analysis.get("isBargain");

                return Boolean.TRUE.equals(isBargain);
            })

            // ordenar por score DESC
            .sorted((a, b) -> {
                Map<String, Object> analysisA = a.getAnalysis();
                Map<String, Object> analysisB = b.getAnalysis();

                Integer scoreA = readInteger(analysisA != null ? analysisA.get("investmentScore") : null);

                Integer scoreB = readInteger(analysisB != null ? analysisB.get("investmentScore") : null);

                return scoreB.compareTo(scoreA);
            })

            // limitar cantidad
            .limit(20)

            .toList();
        }

    private UpsertResult upsertOpportunity(OpportunityRequest request) {
        var analysis = scoringService.calculate(
                request.getPrice(),
                request.getTechnicalDetails()
        );

        Opportunity opportunity = opportunityRepository
                .findByOriginalUrl(request.getOriginalUrl())
                .map(existing -> merge(existing, request, analysis))
                .orElseGet(() -> buildNewOpportunity(request, analysis));

        boolean created = opportunity.getId() == null;
        Opportunity saved = opportunityRepository.save(opportunity);

        return new UpsertResult(saved, created);
    }

    private Opportunity merge(Opportunity existing, OpportunityRequest request, Map<String, Object> analysis) {
        existing.setTitle(request.getTitle());
        existing.setPrice(request.getPrice());
        existing.setCurrency(normalizeCurrency(request.getCurrency()));
        existing.setDescription(request.getDescription());
        existing.setImages(request.getImages());
        existing.setTechnicalDetails(request.getTechnicalDetails());
        existing.setAnalysis(analysis);
        existing.setSubcategoryId(request.getSubcategoryId());
        existing.setLocationId(request.getLocationId());
        existing.setIsActive(request.getActive() != null ? request.getActive() : Boolean.TRUE);
        return existing;
    }

    private Opportunity buildNewOpportunity(OpportunityRequest request, Map<String, Object> analysis) {
        return Opportunity.builder()
                .title(request.getTitle())
                .price(request.getPrice())
                .currency(normalizeCurrency(request.getCurrency()))
                .originalUrl(request.getOriginalUrl())
                .description(request.getDescription())
                .images(request.getImages())
                .technicalDetails(request.getTechnicalDetails())
                .analysis(analysis)
                .subcategoryId(request.getSubcategoryId())
                .locationId(request.getLocationId())
                .isActive(request.getActive() != null ? request.getActive() : Boolean.TRUE)
                .build();
    }

    private String normalizeCurrency(String currency) {
        return currency == null || currency.isBlank() ? "COP" : currency;
    }

    private Integer readInteger(Object value) {
        if (value instanceof Number number) {
            return number.intValue();
        }

        try {
            return value != null ? Integer.parseInt(value.toString()) : 0;
        } catch (NumberFormatException exception) {
            return 0;
        }
    }

    private record UpsertResult(Opportunity opportunity, boolean created) {
    }
}
