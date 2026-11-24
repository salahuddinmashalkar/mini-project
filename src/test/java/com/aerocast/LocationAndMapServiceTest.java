package com.aerocast;

import com.aerocast.model.GeoLocation;
import com.aerocast.model.UnitSystem;
import com.aerocast.model.WeatherReport;
import com.aerocast.service.*;
import com.aerocast.ui.components.WorldWeatherMapPanel;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LocationAndMapServiceTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final AdvisoryEngineService advisoryEngineService = new AdvisoryEngineService();
    private final LocationService locationService = new LocationService(objectMapper);
    private final SampleWeatherService sampleService = new SampleWeatherService(advisoryEngineService, locationService);
    private final WeatherCacheService cacheService = new WeatherCacheService(15);
    private final OpenMeteoWeatherService liveService = new OpenMeteoWeatherService(
            objectMapper, advisoryEngineService, sampleService, cacheService, locationService
    );
    private final UnitConversionService conversionService = new UnitConversionService();

    @Test
    void testSampleWeatherByCoordinates() {
        // Tokyo coordinates
        WeatherReport report = sampleService.getWeatherByCoordinates(35.6762, 139.6503, "Tokyo", "Japan");
        assertNotNull(report);
        assertEquals("Tokyo", report.cityName());
        assertTrue(report.currentTemp() > -50 && report.currentTemp() < 60);
        assertEquals(35.6762, report.latitude(), 0.001);
        assertEquals(139.6503, report.longitude(), 0.001);
    }

    @Test
    void testOpenMeteoWeatherByCoordinates() {
        // Test coordinate retrieval from Open-Meteo
        WeatherReport report = liveService.getWeatherByCoordinates(51.5074, -0.1278, "London", "United Kingdom");
        assertNotNull(report);
        assertEquals("London", report.cityName());
        assertTrue(report.currentTemp() > -50 && report.currentTemp() < 60);
        assertNotNull(report.hourlyForecasts());
        assertNotNull(report.dailyForecasts());
    }

    @Test
    void testLocationServiceFallback() {
        GeoLocation loc = locationService.getIpLocation();
        if (loc != null) {
            assertNotNull(loc.cityName());
            assertTrue(loc.latitude() >= -90 && loc.latitude() <= 90);
            assertTrue(loc.longitude() >= -180 && loc.longitude() <= 180);
        }
    }

    @Test
    void testWorldWeatherMapPanelInitialization() {
        WorldWeatherMapPanel panel = new WorldWeatherMapPanel(conversionService, city -> {});
        assertNotNull(panel);
        WeatherReport report = sampleService.getWeather("Reykjavik", "Iceland");
        assertDoesNotThrow(() -> panel.update(report, UnitSystem.METRIC));
    }

    @Test
    void testSearchWorldwideCitiesAndVillages() {
        // Search town
        List<GeoLocation> townResults = locationService.searchLocations("Udupi");
        assertNotNull(townResults);
        assertFalse(townResults.isEmpty());
        assertTrue(townResults.stream().anyMatch(g -> g.cityName().toLowerCase().contains("udupi")));

        // Search village
        List<GeoLocation> villageResults = locationService.searchLocations("Kinnigoli");
        assertNotNull(villageResults);
        assertFalse(villageResults.isEmpty());
        assertTrue(villageResults.stream().anyMatch(g -> g.cityName().toLowerCase().contains("kinnigoli")));

        // Verify full display name
        GeoLocation first = villageResults.get(0);
        assertNotNull(first.getDisplayName());
        assertTrue(first.getDisplayName().length() > 0);
    }
}
