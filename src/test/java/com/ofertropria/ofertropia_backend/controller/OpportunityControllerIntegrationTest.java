package com.ofertropria.ofertropia_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ofertropria.ofertropia_backend.repository.OpportunityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OpportunityControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OpportunityRepository opportunityRepository;

    @BeforeEach
    void cleanUp() {
        opportunityRepository.deleteAll();
    }

    @Test
    void shouldIngestIdempotentlyAndExposeListings() throws Exception {
        Map<String, Object> firstPayload = Map.of(
                "opportunities", List.of(
                        Map.of(
                                "title", "Apartamento 1",
                                "currency", "COP",
                                "price", 180000000,
                                "originalUrl", "https://example.com/a1",
                                "description", "desc 1",
                                "technicalDetails", Map.of("area_m2", 80, "rooms", 3, "bathrooms", 2, "parking", 1),
                                "active", true
                        ),
                        Map.of(
                                "title", "Apartamento 2",
                                "currency", "COP",
                                "price", 250000000,
                                "originalUrl", "https://example.com/a2",
                                "description", "desc 2",
                                "technicalDetails", Map.of("area_m2", 40, "rooms", 1, "bathrooms", 1),
                                "active", true
                        )
                )
        );

        Map<String, Object> duplicatePayload = Map.of(
                "opportunities", List.of(
                        Map.of(
                                "title", "Apartamento 1 actualizado",
                                "currency", "COP",
                                "price", 175000000,
                                "originalUrl", "https://example.com/a1",
                                "description", "desc nueva",
                                "technicalDetails", Map.of("area_m2", 85, "rooms", 3, "bathrooms", 2, "parking", 1),
                                "active", true
                        )
                )
        );

        mockMvc.perform(post("/api/v1/opportunities/ingest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(firstPayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.received").value(2))
                .andExpect(jsonPath("$.created").value(2))
                .andExpect(jsonPath("$.updated").value(0));

        mockMvc.perform(post("/api/v1/opportunities/ingest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicatePayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.created").value(0))
                .andExpect(jsonPath("$.updated").value(1))
                .andExpect(jsonPath("$.items[0].status").value("updated"));

        mockMvc.perform(get("/api/v1/opportunities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[?(@.originalUrl=='https://example.com/a1')].title").value("Apartamento 1 actualizado"));

        mockMvc.perform(get("/api/v1/opportunities/top-bargains"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].originalUrl").value("https://example.com/a1"))
                .andExpect(jsonPath("$[0].analysis.investmentScore").value(100));
    }

    @Test
    void shouldExposeCorsHeadersForFrontend() throws Exception {
        mockMvc.perform(options("/api/v1/opportunities")
                        .header("Origin", "http://localhost:3000")
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://localhost:3000"));
    }

    @Test
    void shouldFetchOpportunityById() throws Exception {
        Map<String, Object> payload = Map.of(
                "opportunities", List.of(
                        Map.of(
                                "title", "Apartamento 1",
                                "currency", "COP",
                                "price", 180000000,
                                "originalUrl", "https://example.com/a1",
                                "description", "desc 1",
                                "technicalDetails", Map.of("area_m2", 80, "rooms", 3, "bathrooms", 2, "parking", 1),
                                "active", true
                        )
                )
        );

        MvcResult ingestResult = mockMvc.perform(post("/api/v1/opportunities/ingest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isOk())
                .andReturn();

        Map<?, ?> responseBody = objectMapper.readValue(ingestResult.getResponse().getContentAsString(), Map.class);
        List<?> items = (List<?>) responseBody.get("items");
        Map<?, ?> firstItem = (Map<?, ?>) items.get(0);
        UUID id = UUID.fromString(firstItem.get("id").toString());

        mockMvc.perform(get("/api/v1/opportunities/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.originalUrl").value("https://example.com/a1"));
    }

    @Test
    void shouldReturnBadRequestForInvalidUuid() throws Exception {
        mockMvc.perform(get("/api/v1/opportunities/{id}", "not-a-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid request parameter"));
    }
}
