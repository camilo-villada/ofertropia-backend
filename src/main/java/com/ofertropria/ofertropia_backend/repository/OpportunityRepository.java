package com.ofertropria.ofertropia_backend.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ofertropria.ofertropia_backend.model.Opportunity;

@Repository
public interface OpportunityRepository extends JpaRepository<Opportunity, UUID> {
    // Aquí puedes agregar métodos personalizados de consulta si es necesario
}   