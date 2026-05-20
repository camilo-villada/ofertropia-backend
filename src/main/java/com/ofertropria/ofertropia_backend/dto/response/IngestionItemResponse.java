package com.ofertropria.ofertropia_backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class IngestionItemResponse {

    private UUID id;
    private String originalUrl;
    private String status;
    private Integer investmentScore;
    private Boolean bargain;
}
