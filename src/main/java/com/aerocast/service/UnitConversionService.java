package com.aerocast.service;

import com.aerocast.model.DailyForecast;
import com.aerocast.model.HourlyForecast;
import com.aerocast.model.UnitSystem;
import com.aerocast.model.WeatherReport;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * High-precision mathematical conversions between Metric and Imperial meteorological units.
 */
@Service
public class UnitConversionService {

    public double convertTemperature(double celsius, UnitSystem target) {
        if (target == UnitSystem.METRIC) {
            return celsius;
        }
        return (celsius * 9.0 / 5.0) + 32.0;
    }

    public double convertSpeed(double kmh, UnitSystem target) {
        if (target == UnitSystem.METRIC) {
            return kmh;
        }
        return kmh * 0.621371;
    }

    public double convertPressure(double hpa, UnitSystem target) {
        if (target == UnitSystem.METRIC) {
            return hpa;
        }
        return hpa * 0.0295299830714;
    }

    public double convertDistance(double km, UnitSystem target) {
        if (target == UnitSystem.METRIC) {
            return km;
        }
        return km * 0.621371;
    }

    public double convertPrecipitation(double mm, UnitSystem target) {
        if (target == UnitSystem.METRIC) {
            return mm;
        }
        return mm / 25.4;
    }

    public String formatTemperature(double celsius, UnitSystem unit) {
        double val = convertTemperature(celsius, unit);
        return String.format("%.0f%s", val, unit.getTemperatureUnit());
    }

    public String formatSpeed(double kmh, UnitSystem unit) {
        double val = convertSpeed(kmh, unit);
        return String.format("%.1f %s", val, unit.getSpeedUnit());
    }

    public String formatPressure(double hpa, UnitSystem unit) {
        double val = convertPressure(hpa, unit);
        if (unit == UnitSystem.IMPERIAL) {
            return String.format("%.2f %s", val, unit.getPressureUnit());
        }
        return String.format("%.0f %s", val, unit.getPressureUnit());
    }

    public String formatDistance(double km, UnitSystem unit) {
        double val = convertDistance(km, unit);
        return String.format("%.1f %s", val, unit.getDistanceUnit());
    }

    public String formatPrecipitation(double mm, UnitSystem unit) {
        double val = convertPrecipitation(mm, unit);
        if (unit == UnitSystem.IMPERIAL) {
            return String.format("%.2f %s", val, unit.getPrecipitationUnit());
        }
        return String.format("%.1f %s", val, unit.getPrecipitationUnit());
    }

    /**
     * Clones report with all numeric metrics transformed into the target unit system.
     */
    public WeatherReport convertReport(WeatherReport source, UnitSystem target) {
        if (target == UnitSystem.METRIC) {
            return source;
        }

        List<HourlyForecast> convertedHourly = source.hourlyForecasts().stream().map(h ->
                new HourlyForecast(
                        h.time(),
                        convertTemperature(h.temperature(), target),
                        convertTemperature(h.apparentTemperature(), target),
                        h.precipitationProbability(),
                        convertPrecipitation(h.precipitationMm(), target),
                        h.condition(),
                        h.humidity(),
                        convertSpeed(h.windSpeedKmh(), target),
                        h.uvIndex(),
                        h.isDaytime()
                )
        ).toList();

        List<DailyForecast> convertedDaily = source.dailyForecasts().stream().map(d ->
                new DailyForecast(
                        d.date(),
                        d.dayOfWeek(),
                        convertTemperature(d.tempMin(), target),
                        convertTemperature(d.tempMax(), target),
                        d.condition(),
                        d.precipitationProbability(),
                        convertPrecipitation(d.totalPrecipitationMm(), target),
                        convertSpeed(d.maxWindSpeedKmh(), target),
                        d.uvIndexMax()
                )
        ).toList();

        return new WeatherReport(
                source.cityName(),
                source.country(),
                source.latitude(),
                source.longitude(),
                source.observationTime(),
                convertTemperature(source.currentTemp(), target),
                convertTemperature(source.feelsLikeTemp(), target),
                convertTemperature(source.tempMin(), target),
                convertTemperature(source.tempMax(), target),
                source.condition(),
                source.conditionDescription(),
                source.humidity(),
                convertTemperature(source.dewPoint(), target),
                convertSpeed(source.windSpeedKmh(), target),
                source.windDirectionDegrees(),
                source.windCardinal(),
                convertPressure(source.pressureHpa(), target),
                convertDistance(source.visibilityKm(), target),
                source.uvIndex(),
                source.airQuality(),
                source.astronomy(),
                convertedHourly,
                convertedDaily,
                source.advisories(),
                source.isLiveApi(),
                source.dataSourceDescription(),
                source.aiAnalysisSummary()
        );
    }
}
