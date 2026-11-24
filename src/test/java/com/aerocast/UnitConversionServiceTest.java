package com.aerocast;

import com.aerocast.model.UnitSystem;
import com.aerocast.service.UnitConversionService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UnitConversionServiceTest {

    private final UnitConversionService conversionService = new UnitConversionService();

    @Test
    void testTemperatureConversion() {
        // 0°C -> 32°F
        assertEquals(32.0, conversionService.convertTemperature(0.0, UnitSystem.IMPERIAL), 0.001);
        // 100°C -> 212°F
        assertEquals(212.0, conversionService.convertTemperature(100.0, UnitSystem.IMPERIAL), 0.001);
        // Metric identity
        assertEquals(25.5, conversionService.convertTemperature(25.5, UnitSystem.METRIC), 0.001);
    }

    @Test
    void testSpeedConversion() {
        // 100 km/h -> 62.1371 mph
        assertEquals(62.1371, conversionService.convertSpeed(100.0, UnitSystem.IMPERIAL), 0.01);
        // Metric identity
        assertEquals(50.0, conversionService.convertSpeed(50.0, UnitSystem.METRIC), 0.001);
    }

    @Test
    void testPressureConversion() {
        // 1013.25 hPa -> ~29.92 inHg
        assertEquals(29.92, conversionService.convertPressure(1013.25, UnitSystem.IMPERIAL), 0.02);
    }
}
