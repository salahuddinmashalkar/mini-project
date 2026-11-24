package com.aerocast.model;

import java.time.LocalDateTime;

/**
 * Weather metrics at an hourly resolution.
 */
public record HourlyForecast(
        LocalDateTime time,
        double temperature,
        double apparentTemperature,
        int precipitationProbability,
        double precipitationMm,
        WeatherCondition condition,
        int humidity,
        double windSpeedKmh,
        int uvIndex,
        boolean isDaytime
) {}
