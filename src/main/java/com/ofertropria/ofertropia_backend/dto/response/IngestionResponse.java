package com.ofertropria.ofertropia_backend.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class IngestionResponse {

    private int received;
    private int created;
    private int updated;
    private List<IngestionItemResponse> items;
}
