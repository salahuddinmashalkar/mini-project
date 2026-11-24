package com.aerocast.service;

import com.aerocast.model.WeatherReport;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.*;

/**
 * AI Meteorological Analysis Service integrating with Groq API via pure Java HttpClient.
 * Features automatic model discovery and fallback (e.g. qwen/qwen3.8-27b, openai/gpt-oss-120b).
 */
@Service
public class GroqWeatherAiService {

    private static final Logger log = LoggerFactory.getLogger(GroqWeatherAiService.class);
    private static final String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String GROQ_MODELS_URL = "https://api.groq.com/openai/v1/models";

    // Preferred candidate models in priority order
    private static final List<String> PREFERRED_MODELS = List.of(
            "qwen/qwen3.8-27b",
            "openai/gpt-oss-120b",
            "allam-2-7b",
            "llama-3.3-70b-versatile",
            "llama-3.1-8b-instant"
    );

    private final UserPreferenceService preferenceService;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    private volatile String resolvedModel = "qwen/qwen3.8-27b";

    public GroqWeatherAiService(UserPreferenceService preferenceService, ObjectMapper objectMapper) {
        this.preferenceService = preferenceService;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(6))
                .build();
    }

    public String getActiveModelName() {
        return resolvedModel;
    }

    /**
     * Synthesize a smart meteorological briefing if the user has provided a Groq API Key.
     */
    public String generateBriefing(WeatherReport report) {
        Optional<String> apiKeyOpt = preferenceService.getGroqApiKey();
        if (apiKeyOpt.isEmpty() || apiKeyOpt.get().isBlank()) {
            return "Meteorological briefing on standby. Add your Groq API key in Settings to activate automated analysis.";
        }

        String apiKey = apiKeyOpt.get().trim();

        // 1. First attempt with resolvedModel
        String briefing = callGroqChat(report, apiKey, resolvedModel);
        if (briefing != null) {
            return briefing;
        }

        // 2. If initial model failed (e.g. model_not_found), query available models dynamically
        String discoveredModel = discoverAvailableModel(apiKey);
        if (discoveredModel != null && !discoveredModel.equalsIgnoreCase(resolvedModel)) {
            log.info("Switching Groq model to dynamically discovered model: {}", discoveredModel);
            this.resolvedModel = discoveredModel;
            briefing = callGroqChat(report, apiKey, resolvedModel);
            if (briefing != null) {
                return briefing;
            }
        }

        return "Unable to generate meteorological briefing. Please verify your Groq API key in Settings.";
    }

    private String callGroqChat(WeatherReport report, String apiKey, String modelName) {
        try {
            String prompt = String.format(
                    "Act as an expert Chief Meteorologist. Provide a crisp, 2-3 sentence executive briefing for %s, %s. " +
                    "Current Weather: %.1f°C (Feels like %.1f°C), Condition: %s, Humidity: %d%%, Wind: %.1f km/h (%s), UV Index: %d, AQI: %d (%s). " +
                    "Highlight atmospheric dynamics, comfort window, and any vital cautions for today in a sophisticated, authoritative tone.",
                    report.cityName(), report.country(), report.currentTemp(), report.feelsLikeTemp(),
                    report.conditionDescription(), report.humidity(), report.windSpeedKmh(), report.windCardinal(),
                    report.uvIndex(), report.airQuality().aqiUs(), report.airQuality().category()
            );

            Map<String, Object> payload = new HashMap<>();
            payload.put("model", modelName);
            payload.put("temperature", 0.3);
            payload.put("max_tokens", 180);
            payload.put("messages", List.of(
                    Map.of("role", "system", "content", "You are an elite atmospheric scientist delivering executive weather briefings. Be insightful, concise, and professional. Respond with the briefing only."),
                    Map.of("role", "user", "content", prompt)
            ));

            String requestBody = objectMapper.writeValueAsString(payload);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(GROQ_API_URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .timeout(Duration.ofSeconds(10))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                JsonNode root = objectMapper.readTree(response.body());
                JsonNode choices = root.path("choices");
                if (choices.isArray() && !choices.isEmpty()) {
                    String content = choices.get(0).path("message").path("content").asText().trim();
                    if (!content.isBlank()) {
                        return content;
                    }
                }
            } else {
                log.warn("Groq API error HTTP {} for model {}: {}", response.statusCode(), modelName, response.body());
            }
        } catch (Exception e) {
            log.warn("Failed Groq chat request for model {}: {}", modelName, e.getMessage());
        }
        return null;
    }

    private String discoverAvailableModel(String apiKey) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(GROQ_MODELS_URL))
                    .header("Authorization", "Bearer " + apiKey)
                    .timeout(Duration.ofSeconds(6))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonNode root = objectMapper.readTree(response.body());
                JsonNode data = root.path("data");
                if (data.isArray()) {
                    Set<String> availableIds = new HashSet<>();
                    for (JsonNode item : data) {
                        String id = item.path("id").asText();
                        if (id != null && !id.isBlank()) {
                            availableIds.add(id.toLowerCase());
                        }
                    }

                    // Pick highest priority available model
                    for (String preferred : PREFERRED_MODELS) {
                        if (availableIds.contains(preferred.toLowerCase())) {
                            return preferred;
                        }
                    }

                    // Fallback to any active non-whisper/non-guard model
                    for (JsonNode item : data) {
                        String id = item.path("id").asText();
                        if (!id.contains("whisper") && !id.contains("guard") && !id.contains("orpheus")) {
                            return id;
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Could not discover Groq models: {}", e.getMessage());
        }
        return "qwen/qwen3.8-27b";
    }
}
