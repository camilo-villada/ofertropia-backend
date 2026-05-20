package com.ofertropria.ofertropia_backend.service.scoring;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Service
public class InvestmentScoringService {

    public Map<String, Object> calculate(
            BigDecimal price,
            Map<String, Object> technicalDetails
    ) {

        Map<String, Object> analysis = new HashMap<>();

        BigDecimal safePrice = price != null ? price : BigDecimal.ZERO;
        Map<String, Object> safeTechnicalDetails =
                technicalDetails != null ? technicalDetails : Map.of();

        double area = getDouble(safeTechnicalDetails.get("area_m2"));
        int rooms = getInt(safeTechnicalDetails.get("rooms"));
        int bathrooms = getInt(safeTechnicalDetails.get("bathrooms"));
        int parking = getInt(safeTechnicalDetails.get("parking"));

        Double pricePerM2 = area > 0 ? safePrice.doubleValue() / area : null;

        int score = 50;

        if (safePrice.compareTo(BigDecimal.ZERO) <= 0) {
            score -= 20;
        }

        // precio por m2 barato
        if (pricePerM2 != null && pricePerM2 < 3000000) {
            score += 25;
        }

        // habitaciones
        if (rooms >= 3) {
            score += 10;
        }

        // baños
        if (bathrooms >= 2) {
            score += 10;
        }

        // parqueadero
        if (parking >= 1) {
            score += 5;
        }

        if (score < 0) {
            score = 0;
        }

        if (score > 100) {
            score = 100;
        }

        String badge = "NORMAL";

        if (score >= 90) {
            badge = "ULTRA GANGA";
        } else if (score >= 75) {
            badge = "GANGA";
        } else if (score >= 60) {
            badge = "BUENA OPORTUNIDAD";
        }

        analysis.put("investmentScore", score);
        analysis.put("badge", badge);
        analysis.put("isBargain", score >= 75);
        analysis.put("pricePerM2", pricePerM2);
        analysis.put("scoringVersion", "mvp-v1");
        analysis.put("hasAreaData", area > 0);
        analysis.put("dataCompleteness", buildCompletenessLabel(area, rooms, bathrooms));

        return analysis;
    }

    private double getDouble(Object value) {
        if (value == null) {
            return 0;
        }

        try {
            if (value instanceof Number number) {
                return number.doubleValue();
            }

            String normalized = value.toString()
                    .trim()
                    .replace(",", ".")
                    .replaceAll("[^\\d.\\-]", "");

            return normalized.isBlank() ? 0 : Double.parseDouble(normalized);
        } catch (NumberFormatException exception) {
            return 0;
        }
    }

    private int getInt(Object value) {
        if (value == null) {
            return 0;
        }

        try {
            if (value instanceof Number number) {
                return number.intValue();
            }

            String normalized = value.toString()
                    .trim()
                    .toLowerCase(Locale.ROOT)
                    .replaceAll("[^\\d\\-]", "");

            return normalized.isBlank() ? 0 : Integer.parseInt(normalized);
        } catch (NumberFormatException exception) {
            return 0;
        }
    }

    private String buildCompletenessLabel(double area, int rooms, int bathrooms) {
        int filledFields = 0;

        if (area > 0) {
            filledFields++;
        }

        if (rooms > 0) {
            filledFields++;
        }

        if (bathrooms > 0) {
            filledFields++;
        }

        return switch (filledFields) {
            case 3 -> "HIGH";
            case 2 -> "MEDIUM";
            default -> "LOW";
        };
    }
}
