package com.ofertropria.ofertropia_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.List;

@Service
public class AiService {

    private static final Logger log = LoggerFactory.getLogger(AiService.class);

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public String analyzeOpportunity(String title, String description, double price) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("GEMINI_API_KEY no configurada; se omite llamada a Gemini");
            return "{\"isGanga\": false, \"score\": 0, \"reason\": \"GEMINI_API_KEY no configurada\"}";
        }

        String prompt = String.format(
            "Analiza si este producto en Colombia es una GANGA: " +
            "Título: %s, Precio: %s, Descripción: %s. " +
            "Responde SOLO un JSON: {\"isGanga\": boolean, \"score\": 0-10, \"reason\": \"explicación\"}",
            title, price, description
        );

        // 1. Configuramos los Headers (La pieza que faltaba)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 2. Creamos el cuerpo del JSON
        Map<String, Object> requestBody = Map.of(
            "contents", List.of(
                Map.of("parts", List.of(
                    Map.of("text", prompt)
                ))
            )
        );

        // 3. Metemos todo en una "Entidad" (Cuerpo + Cabeceras)
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            String url = apiUrl + "?key=" + apiKey;
            // 4. Enviamos la petición completa
            return restTemplate.postForObject(url, entity, String.class);
            
        } catch (HttpStatusCodeException e) {
            // Esto nos dirá si Google nos rechazó por llave inválida, falta de cuota, etc.
            log.warn("Error de Google (status={}): {}", e.getStatusCode(), e.getResponseBodyAsString());
            return "{\"isGanga\": false, \"reason\": \"Error API: " + e.getStatusCode() + "\"}";
        } catch (Exception e) {
            log.warn("Error de conexión hacia Gemini: {}", e.getMessage());
            return "{\"isGanga\": false, \"reason\": \"Error conexión: " + e.getMessage() + "\"}";
        }
    }
}