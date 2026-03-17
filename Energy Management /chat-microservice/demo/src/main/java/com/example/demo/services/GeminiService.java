package com.example.demo.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GeminiService.class);

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate;

    public GeminiService() {
        this.restTemplate = new RestTemplate();
    }

    public String getAIResponse(String userMessage) {
        try {
            if (apiKey == null) {
                LOGGER.error("EROARE: API Key-ul nu este setat corect in docker-compose!");
                return "System configuration error: Invalid API Key.";
            }

            String url = apiUrl + "?key=" + apiKey;

            LOGGER.info("Sending request to Gemini AI...");

            Map<String, Object> textPart = new HashMap<>();
            textPart.put("text", "You are a helpful assistant. Answer shortly: " + userMessage);

            Map<String, Object> contentPart = new HashMap<>();
            contentPart.put("parts", Collections.singletonList(textPart));

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("contents", Collections.singletonList(contentPart));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            if (response.getBody() != null && response.getBody().containsKey("candidates")) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
                if (!candidates.isEmpty()) {
                    Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                    List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                    if (!parts.isEmpty()) {
                        String answer = (String) parts.get(0).get("text");
                        LOGGER.info("Gemini responded successfully.");
                        return answer;
                    }
                }
            }
            return "I'm having trouble thinking right now.";

        } catch (HttpClientErrorException e) {
            LOGGER.error("Eroare HTTP de la Gemini: Status {}, Body: {}", e.getStatusCode(), e.getResponseBodyAsString());
            return "AI Error: " + e.getStatusCode();
        } catch (Exception e) {
            LOGGER.error("Eroare generica in GeminiService", e);
            return "AI Service is currently unavailable.";
        }
    }
}