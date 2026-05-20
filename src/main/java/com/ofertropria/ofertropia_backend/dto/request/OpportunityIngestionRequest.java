package com.ofertropria.ofertropia_backend.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class OpportunityIngestionRequest {

    @Valid
    @NotEmpty(message = "At least one opportunity is required")
    private List<OpportunityRequest> opportunities;
}
