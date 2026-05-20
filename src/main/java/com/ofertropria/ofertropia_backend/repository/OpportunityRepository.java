package com.ofertropria.ofertropia_backend.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ofertropria.ofertropia_backend.entity.Opportunity;

@Repository
public interface OpportunityRepository extends JpaRepository<Opportunity, UUID> {

    List<Opportunity> findByIsActiveTrue();
    Optional<Opportunity> findByOriginalUrl(String originalUrl);
} 
