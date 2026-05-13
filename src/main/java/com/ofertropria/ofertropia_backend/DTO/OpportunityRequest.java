package com.ofertropria.ofertropia_backend.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data 
public class OpportunityRequest {
    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Price is required")
    private BigDecimal price;

    @NotBlank(message = "URL is required")
    private String originalUrl;

    private String description;
    private List<String> images;
    private Map<String, Object> technicalDetails;
    private Integer subcategoryId;
    private Integer locationId;
}