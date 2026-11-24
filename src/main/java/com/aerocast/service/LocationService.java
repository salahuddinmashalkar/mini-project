package com.aerocast.service;

import com.aerocast.model.GeoLocation;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Universal Geolocation, Search, and Reverse-Geocoding Engine.
 * Supports:
 * 1. Global Geocoding via Open-Meteo Geocoding Database (covers millions of global cities, towns, and regions).
 * 2. Deep Village & Hamlet Geocoding via OpenStreetMap Nominatim (covers small rural villages, hamlets, and settlements worldwide).
 * 3. High-accuracy IP network geolocation fallback for instant local weather.
 * 4. In-memory caching for sub-millisecond query responses.
 */
@Service
public class LocationService {

    private static final Logger log = LoggerFactory.getLogger(LocationService.class);

    private static final String IP_GEOLOCATION_URL = "http://ip-api.com/json/";
    private static final String OPEN_METEO_GEOCODE_URL = "https://geocoding-api.open-meteo.com/v1/search";
    private static final String NOMINATIM_SEARCH_URL = "https://nominatim.openstreetmap.org/search";
    private static final String NOMINATIM_REVERSE_URL = "https://nominatim.openstreetmap.org/reverse?format=json&lat=%.4f&lon=%.4f";

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final Map<String, List<GeoLocation>> searchCache = new ConcurrentHashMap<>();

    private static final List<GeoLocation> DEFAULT_LOCATIONS = List.of(
            new GeoLocation("Tokyo", "Japan", "Tokyo", 35.6762, 139.6503, "Asia/Tokyo"),
            new GeoLocation("London", "United Kingdom", "England", 51.5074, -0.1278, "Europe/London"),
            new GeoLocation("New York", "United States", "New York", 40.7128, -74.0060, "America/New_York"),
            new GeoLocation("Mumbai", "India", "Maharashtra", 19.0760, 72.8777, "Asia/Kolkata"),
            new GeoLocation("Paris", "France", "Île-de-France", 48.8566, 2.3522, "Europe/Paris"),
            new GeoLocation("Sydney", "Australia", "New South Wales", -33.8688, 151.2093, "Australia/Sydney"),
            new GeoLocation("Cairo", "Egypt", "Cairo", 30.0444, 31.2357, "Africa/Cairo"),
            new GeoLocation("Reykjavik", "Iceland", "Capital Region", 64.1466, -21.9426, "Atlantic/Reykjavik"),
            new GeoLocation("Dubai", "United Arab Emirates", "Dubai", 25.2048, 55.2708, "Asia/Dubai"),
            new GeoLocation("Singapore", "Singapore", "", 1.3521, 103.8198, "Asia/Singapore"),
            new GeoLocation("São Paulo", "Brazil", "São Paulo", -23.5505, -46.6333, "America/Sao_Paulo")
    );

    public LocationService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    /**
     * Search all global cities, towns, and villages worldwide.
     * Queries Open-Meteo Geocoding, with automatic fallback and enrichment
     * through OpenStreetMap Nominatim for villages, hamlets, and rural areas.
     */
    public List<GeoLocation> searchLocations(String query) {
        if (query == null || query.isBlank()) {
            return DEFAULT_LOCATIONS;
        }

        String q = query.trim();
        String cacheKey = q.toLowerCase(Locale.ROOT);
        List<GeoLocation> cached = searchCache.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        List<GeoLocation> results = new ArrayList<>();
        Set<String> seenCoords = new HashSet<>();

        // 1. Primary Engine: Open-Meteo Global Geocoding API (Fast, multi-lingual, high capacity)
        try {
            String encoded = URLEncoder.encode(q, StandardCharsets.UTF_8);
            String url = String.format("%s?name=%s&count=10&language=en&format=json", OPEN_METEO_GEOCODE_URL, encoded);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "AeroCast/1.0")
                    .timeout(Duration.ofSeconds(4))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonNode root = objectMapper.readTree(response.body());
                JsonNode items = root.path("results");
                if (items.isArray()) {
                    for (JsonNode item : items) {
                        String name = item.path("name").asText("");
                        String country = item.path("country").asText("");
                        String admin1 = item.path("admin1").asText("");
                        double lat = item.path("latitude").asDouble();
                        double lon = item.path("longitude").asDouble();
                        String tz = item.path("timezone").asText("auto");

                        String coordKey = String.format(Locale.US, "%.2f,%.2f", lat, lon);
                        if (seenCoords.add(coordKey)) {
                            results.add(new GeoLocation(name, country, admin1, lat, lon, tz));
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Open-Meteo geocoding query failed for '{}': {}", q, e.getMessage());
        }

        // 2. Secondary Engine: OpenStreetMap Nominatim (Crucial for villages, hamlets, and sub-districts)
        if (results.size() < 3) {
            try {
                String encoded = URLEncoder.encode(q, StandardCharsets.UTF_8);
                String url = String.format("%s?q=%s&format=json&addressdetails=1&limit=8", NOMINATIM_SEARCH_URL, encoded);
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("User-Agent", "AeroCastWeatherApp/1.0 (contact: station@aerocast.local)")
                        .timeout(Duration.ofSeconds(4))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() == 200) {
                    JsonNode root = objectMapper.readTree(response.body());
                    if (root.isArray()) {
                        for (JsonNode item : root) {
                            JsonNode address = item.path("address");
                            String name = item.path("name").asText("");
                            if (name.isBlank() && address.isObject()) {
                                name = address.path("village").asText("");
                                if (name.isBlank()) name = address.path("town").asText("");
                                if (name.isBlank()) name = address.path("hamlet").asText("");
                                if (name.isBlank()) name = address.path("suburb").asText("");
                                if (name.isBlank()) name = address.path("city").asText("");
                            }
                            if (name.isBlank()) {
                                name = item.path("display_name").asText().split(",")[0].trim();
                            }

                            String country = address.path("country").asText("");
                            String admin1 = address.path("state").asText("");
                            if (admin1.isBlank()) admin1 = address.path("county").asText("");
                            if (admin1.isBlank()) admin1 = address.path("state_district").asText("");

                            double lat = item.path("lat").asDouble();
                            double lon = item.path("lon").asDouble();

                            String coordKey = String.format(Locale.US, "%.2f,%.2f", lat, lon);
                            if (seenCoords.add(coordKey)) {
                                results.add(new GeoLocation(name, country, admin1, lat, lon, "auto"));
                            }
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("Nominatim village search failed for '{}': {}", q, e.getMessage());
            }
        }

        // Cache results if not empty
        if (!results.isEmpty()) {
            searchCache.put(cacheKey, Collections.unmodifiableList(results));
        }

        return results;
    }

    /**
     * Resolve user's location via network IP address.
     * Provides an immediate, zero-prompt fallback if device GPS is denied or unavailable.
     */
    public GeoLocation getIpLocation() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(IP_GEOLOCATION_URL))
                    .header("User-Agent", "AeroCast/1.0")
                    .timeout(Duration.ofSeconds(6))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonNode root = objectMapper.readTree(response.body());
                if ("success".equalsIgnoreCase(root.path("status").asText())) {
                    String city = root.path("city").asText("Current Location");
                    String country = root.path("country").asText("");
                    String region = root.path("regionName").asText("");
                    double lat = root.path("lat").asDouble(35.6762);
                    double lon = root.path("lon").asDouble(139.6503);
                    String tz = root.path("timezone").asText("auto");

                    log.info("Resolved IP location: {}, {} (lat: {}, lon: {})", city, country, lat, lon);
                    return new GeoLocation(city, country, region, lat, lon, tz);
                }
            }
        } catch (Exception e) {
            log.warn("IP Geolocation request failed: {}", e.getMessage());
        }

        // Default fallback if offline
        return new GeoLocation("Current Location", "", "", 35.6762, 139.6503, "auto");
    }

    /**
     * Reverse-geocode latitude and longitude into an administrative city, village, and country.
     */
    public GeoLocation reverseGeocode(double latitude, double longitude) {
        try {
            String url = String.format(Locale.US, NOMINATIM_REVERSE_URL, latitude, longitude);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "AeroCast-Weather-Station/1.0 (observatory@aerocast.local)")
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonNode root = objectMapper.readTree(response.body());
                JsonNode address = root.path("address");
                if (!address.isMissingNode()) {
                    String city = address.path("village").asText("");
                    if (city.isBlank()) city = address.path("town").asText("");
                    if (city.isBlank()) city = address.path("city").asText("");
                    if (city.isBlank()) city = address.path("hamlet").asText("");
                    if (city.isBlank()) city = address.path("municipality").asText("");
                    if (city.isBlank()) city = address.path("county").asText("");
                    if (city.isBlank()) city = address.path("state").asText("");
                    if (city.isBlank()) city = String.format(Locale.US, "Station (%.2f°, %.2f°)", latitude, longitude);

                    String country = address.path("country").asText("");
                    String state = address.path("state").asText("");
                    if (state.isBlank()) state = address.path("county").asText("");

                    log.info("Reverse geocoded ({}, {}) -> {}, {}", latitude, longitude, city, country);
                    return new GeoLocation(city, country, state, latitude, longitude, "auto");
                }
            }
        } catch (Exception e) {
            log.warn("Reverse geocode failed for ({}, {}): {}", latitude, longitude, e.getMessage());
        }

        return new GeoLocation(
                String.format(Locale.US, "Location (%.2f°, %.2f°)", latitude, longitude),
                "", "", latitude, longitude, "auto"
        );
    }
}
