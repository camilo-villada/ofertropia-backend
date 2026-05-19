package com.ofertropria.ofertropia_backend.service;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AiService {

    private static final Logger log = LoggerFactory.getLogger(AiService.class);

    public String analyzeOpportunity(String title, String description, double price) {
        log.info("Evaluando con Switch Java 21: {}", title);

        if (title == null) {
            return "{\"isGanga\": false, \"score\": 0, \"reason\": \"Título inválido\"}";
        }

        String textToAnalyze = (title + " " + (description != null ? description : "")).toUpperCase();

        // 1. Ignoramos lo que no sea inmuebles
        if (!textToAnalyze.contains("APARTAMENTO") && !textToAnalyze.contains("APTO") 
            && !textToAnalyze.contains("INMUEBLE") && !textToAnalyze.contains("PENTHOUSE")) {
            return "{\"isGanga\": false, \"score\": 3, \"reason\": \"Ignorado. Solo procesamos Finca Raíz por ahora.\"}";
        }

        // 2. Filtro Anti-Estafas
        if (price < 90000000) {
            return String.format("{\"isGanga\": false, \"score\": 1, \"reason\": \"Alerta: Precio ilógico (%.0f COP). Posible estafa o remate.\"}", price);
        }

        // 3. Evaluamos por zonas usando Switch Expression (Java 21)
        return switch (textToAnalyze) {
            // Zonas Premium
            case String s when s.contains("POBLADO") || s.contains("ENVIGADO") || s.contains("LAURELES") -> {
                if (price <= 350000000) yield formatResult(true, 9, "Ganga crítica en zona premium por debajo de 350M.");
                if (price <= 500000000) yield formatResult(true, 7, "Buena oportunidad en zona premium.");
                yield formatResult(false, 4, "Precio estándar para zona premium.");
            }
            // Zonas Estrato Medio
            case String s when s.contains("BELEN") || s.contains("BELÉN") || s.contains("SABANETA") || s.contains("ITAGUI") -> {
                if (price <= 230000000) yield formatResult(true, 9, "Ganga en sector clase media (Alta rotación).");
                if (price <= 320000000) yield formatResult(true, 7, "Precio competitivo en sector tradicional.");
                yield formatResult(false, 5, "Precio estándar para el mercado.");
            }
            // Zonas Populares / Expansión
            case String s when s.contains("BELLO") || s.contains("ROBLEDO") || s.contains("BUENOS AIRES") -> {
                if (price <= 150000000) yield formatResult(true, 9, "Ganga para inversión en zona de expansión.");
                if (price <= 210000000) yield formatResult(true, 7, "Buen precio para zona norte/noroccidente.");
                yield formatResult(false, 4, "Precio elevado para esta zona.");
            }
            // Caso por defecto (Medellín general)
            default -> {
                if (price <= 180000000) yield formatResult(true, 8, "Posible ganga general por debajo de 180M. Validar zona.");
                yield formatResult(false, 5, "Precio requiere análisis detallado de ubicación.");
            }
        };
    }

    // Método auxiliar para armar el JSON sin repetir código
    private String formatResult(boolean isGanga, int score, String reason) {
        return String.format("{\"isGanga\": %b, \"score\": %d, \"reason\": \"%s\"}", isGanga, score, reason);
    }
}