package com.ofertropria.ofertropia_backend.service.scoring;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class InvestmentScoringServiceTest {

    private final InvestmentScoringService scoringService = new InvestmentScoringService();

    @Test
    void shouldHandleNullsAndZeroAreaWithoutBreaking() {
        Map<String, Object> details = new HashMap<>();
        details.put("area_m2", 0);
        details.put("rooms", null);
        details.put("bathrooms", "N/A");

        Map<String, Object> analysis = scoringService.calculate(
                BigDecimal.valueOf(2_500_000),
                details
        );

        assertEquals(50, analysis.get("investmentScore"));
        assertEquals(false, analysis.get("isBargain"));
        assertNull(analysis.get("pricePerM2"));
        assertEquals("LOW", analysis.get("dataCompleteness"));
    }

    @Test
    void shouldParseMixedValuesAndMarkBargainWhenDataIsGood() {
        Map<String, Object> analysis = scoringService.calculate(
                BigDecimal.valueOf(180_000_000),
                Map.of(
                        "area_m2", "80 m2",
                        "rooms", "3",
                        "bathrooms", "2",
                        "parking", "1"
                )
        );

        assertEquals(100, analysis.get("investmentScore"));
        assertEquals(true, analysis.get("isBargain"));
        assertNotNull(analysis.get("pricePerM2"));
        assertEquals("HIGH", analysis.get("dataCompleteness"));
    }
}
