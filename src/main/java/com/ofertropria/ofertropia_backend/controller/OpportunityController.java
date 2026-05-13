package com.ofertropria.ofertropia_backend.controller;

import com.ofertropria.ofertropia_backend.DTO.OpportunityRequest;
import com.ofertropria.ofertropia_backend.model.Opportunity;
import com.ofertropria.ofertropia_backend.service.OpportunityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/opportunities")
@RequiredArgsConstructor
public class OpportunityController {

    private final OpportunityService opportunityService;

    @PostMapping
    public ResponseEntity<Opportunity> create(@Valid @RequestBody OpportunityRequest request) {
        Opportunity created = opportunityService.processAndSave(request);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Opportunity>> getAll() {
        return ResponseEntity.ok(opportunityService.findAll());
    }
}