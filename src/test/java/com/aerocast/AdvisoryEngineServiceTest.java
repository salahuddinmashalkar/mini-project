package com.aerocast;

import com.aerocast.model.AirQualityMetrics;
import com.aerocast.model.LifestyleAdvisory;
import com.aerocast.model.WeatherCondition;
import com.aerocast.service.AdvisoryEngineService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AdvisoryEngineServiceTest {

    private final AdvisoryEngineService advisoryEngineService = new AdvisoryEngineService();

    @Test
    void testAdvisoryGeneration() {
        AirQualityMetrics aqi = AirQualityMetrics.of(35, 8.0, 15.0, 30.0, 12.0, 2.0);
        List<LifestyleAdvisory> advisories = advisoryEngineService.generateAdvisories(
                22.0, WeatherCondition.CLEAR_SUNNY, 50, 10.0, 4, aqi
        );

        assertNotNull(advisories);
        assertEquals(4, advisories.size());

        // Check categories
        assertTrue(advisories.stream().anyMatch(a -> a.category().equalsIgnoreCase("Clothing")));
        assertTrue(advisories.stream().anyMatch(a -> a.category().equalsIgnoreCase("Hydration")));
        assertTrue(advisories.stream().anyMatch(a -> a.category().equalsIgnoreCase("Fitness")));
        assertTrue(advisories.stream().anyMatch(a -> a.category().equalsIgnoreCase("Commute")));
    }

    @Test
    void testSevereWeatherAdvisory() {
        AirQualityMetrics aqi = AirQualityMetrics.of(180, 55.0, 95.0, 60.0, 20.0, 10.0);
        List<LifestyleAdvisory> advisories = advisoryEngineService.generateAdvisories(
                -5.0, WeatherCondition.SNOW_BLIZZARD, 85, 45.0, 0, aqi
        );

        LifestyleAdvisory clothing = advisories.stream().filter(a -> a.category().equalsIgnoreCase("Clothing")).findFirst().orElseThrow();
        assertEquals("Heavy Thermal", clothing.badgeLevel());

        LifestyleAdvisory fitness = advisories.stream().filter(a -> a.category().equalsIgnoreCase("Fitness")).findFirst().orElseThrow();
        assertTrue(fitness.badgeLevel().contains("Discouraged"));
    }
}
