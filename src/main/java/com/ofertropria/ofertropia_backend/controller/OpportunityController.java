package com.ofertropria.ofertropia_backend.controller;

import com.ofertropria.ofertropia_backend.dto.request.OpportunityIngestionRequest;
import com.ofertropria.ofertropia_backend.dto.response.OpportunityResponse;
import com.ofertropria.ofertropia_backend.dto.response.IngestionResponse;
import com.ofertropria.ofertropia_backend.mapper.OpportunityMapper;
import com.ofertropria.ofertropia_backend.dto.request.OpportunityRequest;
import com.ofertropria.ofertropia_backend.entity.Opportunity;
import com.ofertropria.ofertropia_backend.service.OpportunityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/opportunities")
@RequiredArgsConstructor
public class OpportunityController {

    private final OpportunityService opportunityService;

    @PostMapping
    public ResponseEntity<OpportunityResponse> create(@Valid @RequestBody OpportunityRequest request) {
        Opportunity created = opportunityService.processAndSave(request);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(OpportunityMapper.toResponse(created));
        
    }

    @PostMapping("/ingest")
    public ResponseEntity<IngestionResponse> ingest(@Valid @RequestBody OpportunityIngestionRequest request) {
        return ResponseEntity.ok(opportunityService.ingest(request.getOpportunities()));
    }

    @GetMapping
    public ResponseEntity<List<OpportunityResponse>> getAll() {
        
        List<OpportunityResponse> response = 
            opportunityService.findAll().stream()
                .map(OpportunityMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OpportunityResponse> getById(@PathVariable UUID id) {
        return opportunityService.findById(id)
                .map(OpportunityMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/top-bargains")
    public ResponseEntity<List<OpportunityResponse>> getTopBargains() {
        
        List<OpportunityResponse> response = 
            opportunityService.findTopBargains().stream()
                .map(OpportunityMapper::toResponse)
                .toList();
        return ResponseEntity.ok(response);
    }
}
