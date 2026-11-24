package com.aerocast.model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Root domain model representing the complete meteorological report for a location.
 * Enhanced with geographic coordinates for global map tracking and spatial telemetry.
 */
public record WeatherReport(
        String cityName,
        String country,
        double latitude,
        double longitude,
        LocalDateTime observationTime,
        double currentTemp,
        double feelsLikeTemp,
        double tempMin,
        double tempMax,
        WeatherCondition condition,
        String conditionDescription,
        int humidity,
        double dewPoint,
        double windSpeedKmh,
        int windDirectionDegrees,
        String windCardinal,
        double pressureHpa,
        double visibilityKm,
        int uvIndex,
        AirQualityMetrics airQuality,
        AstronomyData astronomy,
        List<HourlyForecast> hourlyForecasts,
        List<DailyForecast> dailyForecasts,
        List<LifestyleAdvisory> advisories,
        boolean isLiveApi,
        String dataSourceDescription,
        String aiAnalysisSummary
) {
    // Backward-compatible constructor defaulting latitude and longitude to 0.0
    public WeatherReport(
            String cityName,
            String country,
            LocalDateTime observationTime,
            double currentTemp,
            double feelsLikeTemp,
            double tempMin,
            double tempMax,
            WeatherCondition condition,
            String conditionDescription,
            int humidity,
            double dewPoint,
            double windSpeedKmh,
            int windDirectionDegrees,
            String windCardinal,
            double pressureHpa,
            double visibilityKm,
            int uvIndex,
            AirQualityMetrics airQuality,
            AstronomyData astronomy,
            List<HourlyForecast> hourlyForecasts,
            List<DailyForecast> dailyForecasts,
            List<LifestyleAdvisory> advisories,
            boolean isLiveApi,
            String dataSourceDescription,
            String aiAnalysisSummary
    ) {
        this(cityName, country, 0.0, 0.0, observationTime, currentTemp, feelsLikeTemp, tempMin, tempMax,
                condition, conditionDescription, humidity, dewPoint, windSpeedKmh, windDirectionDegrees,
                windCardinal, pressureHpa, visibilityKm, uvIndex, airQuality, astronomy,
                hourlyForecasts, dailyForecasts, advisories, isLiveApi, dataSourceDescription, aiAnalysisSummary);
    }

    public boolean isDaytime() {
        if (astronomy != null) {
            return astronomy.isSunUp();
        }
        int hour = observationTime.getHour();
        return hour >= 6 && hour < 20;
    }
}
