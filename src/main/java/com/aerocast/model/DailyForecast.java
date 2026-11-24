package com.aerocast.model;

import java.time.LocalDate;

/**
 * Weather metrics at a daily resolution.
 */
public record DailyForecast(
        LocalDate date,
        String dayOfWeek,
        double tempMin,
        double tempMax,
        WeatherCondition condition,
        int precipitationProbability,
        double totalPrecipitationMm,
        double maxWindSpeedKmh,
        int uvIndexMax
) {}
