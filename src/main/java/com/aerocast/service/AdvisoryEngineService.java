package com.aerocast.service;

import com.aerocast.model.AirQualityMetrics;
import com.aerocast.model.LifestyleAdvisory;
import com.aerocast.model.WeatherCondition;
import com.aerocast.model.WeatherReport;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Intelligent meteorological rules engine calculating lifestyle, clothing, hydration and travel advisories.
 */
@Service
public class AdvisoryEngineService {

    public List<LifestyleAdvisory> generateAdvisories(
            double tempCelsius,
            WeatherCondition condition,
            int humidity,
            double windSpeedKmh,
            int uvIndex,
            AirQualityMetrics aqi
    ) {
        List<LifestyleAdvisory> list = new ArrayList<>();

        // 1. Clothing & Wardrobe
        list.add(evaluateClothing(tempCelsius, condition, windSpeedKmh));

        // 2. Hydration & Health
        list.add(evaluateHydration(tempCelsius, humidity, uvIndex));

        // 3. Outdoor Fitness / Running
        list.add(evaluateOutdoorFitness(tempCelsius, condition, windSpeedKmh, aqi));

        // 4. Commute & Travel Safety
        list.add(evaluateCommute(condition, windSpeedKmh));

        return list;
    }

    private LifestyleAdvisory evaluateClothing(double temp, WeatherCondition condition, double windKmh) {
        String title = "Wardrobe Recommendation";
        String recommendation;
        String level;
        String color;

        if (condition == WeatherCondition.SNOW_BLIZZARD || temp <= 0) {
            level = "Heavy Thermal";
            color = "#38bdf8";
            recommendation = "Sub-zero conditions: Wear insulated thermal baselayers, down parka, wool gloves, and waterproof snow boots.";
        } else if (temp < 10) {
            level = "Warm Layers";
            color = "#0ea5e9";
            recommendation = "Chilly atmosphere: Medium-weight coat, fleece or cashmere sweater, and closed walking shoes recommended.";
        } else if (condition == WeatherCondition.HEAVY_RAIN || condition == WeatherCondition.LIGHT_RAIN || condition == WeatherCondition.THUNDERSTORM) {
            level = "Waterproof Gear";
            color = "#0284c7";
            recommendation = "Wet precipitation expected: Pack a stormproof umbrella, hooded windbreaker, and water-resistant footwear.";
        } else if (temp > 30) {
            level = "Ultralight Breathable";
            color = "#ef4444";
            recommendation = "High thermal load: Loose-fitting, UV-blocking linen or cotton apparel, polarized sunglasses, and sun hat.";
        } else {
            level = "Comfort Casual";
            color = "#10b981";
            recommendation = "Pleasant ambient temperatures: Light jacket or standard cotton t-shirt with trousers.";
        }

        return new LifestyleAdvisory("Clothing", title, recommendation, level, color, "hanger");
    }

    private LifestyleAdvisory evaluateHydration(double temp, int humidity, int uv) {
        String title = "Hydration & Sun Exposure";
        String recommendation;
        String level;
        String color;

        if (temp >= 32 || (temp >= 28 && humidity > 70)) {
            level = "Critical Hydration";
            color = "#ef4444";
            recommendation = "Elevated heat index: Target at least 3.0L – 3.5L of water intake with electrolytes. Minimize midday sun exposure.";
        } else if (uv >= 8) {
            level = "Extreme UV Warning";
            color = "#f97316";
            recommendation = "UV Index is dangerously high. Apply SPF 50+ broad-spectrum sunscreen every 90 minutes and seek shade.";
        } else if (temp >= 24) {
            level = "Active Hydration";
            color = "#f59e0b";
            recommendation = "Warm climate: Maintain 2.0L – 2.5L daily hydration. Reapply sunscreen if outdoors longer than 30 minutes.";
        } else {
            level = "Standard Hydration";
            color = "#10b981";
            recommendation = "Normal metabolic baseline: Aim for standard 2.0L water intake. Ambient moisture levels are balanced.";
        }

        return new LifestyleAdvisory("Hydration", title, recommendation, level, color, "water");
    }

    private LifestyleAdvisory evaluateOutdoorFitness(double temp, WeatherCondition condition, double windKmh, AirQualityMetrics aqi) {
        String title = "Outdoor Sports & Running";
        String recommendation;
        String level;
        String color;

        int aqiVal = (aqi != null) ? aqi.aqiUs() : 40;

        if (aqiVal > 150) {
            level = "Outdoor Exercise Discouraged";
            color = "#ef4444";
            recommendation = "Poor air quality (AQI " + aqiVal + "): Move running, cycling, and workouts indoors to indoor gym or treadmill.";
        } else if (condition == WeatherCondition.THUNDERSTORM || condition == WeatherCondition.HEAVY_RAIN) {
            level = "Hazardous Weather";
            color = "#ef4444";
            recommendation = "Lightning or torrential rain risk: Outdoor activities pose slip and storm hazards. Postpone outdoor running.";
        } else if (temp > 33) {
            level = "Heat Stress Alert";
            color = "#f97316";
            recommendation = "High heat load: Shift cardio training to early dawn or late evening hours to avoid thermal exhaustion.";
        } else if (temp >= 14 && temp <= 22 && aqiVal <= 60 && windKmh < 25) {
            level = "Prime Conditions (10/10)";
            color = "#10b981";
            recommendation = "Optimal meteorological window: Crisp air, mild temperatures, and low wind make this ideal for running or cycling.";
        } else {
            level = "Moderate Conditions";
            color = "#0ea5e9";
            recommendation = "Suitable for outdoor jogging and recreational walks. Check personal comfort and wind exposure.";
        }

        return new LifestyleAdvisory("Fitness", title, recommendation, level, color, "activity");
    }

    private LifestyleAdvisory evaluateCommute(WeatherCondition condition, double windKmh) {
        String title = "Commute & Road Safety";
        String recommendation;
        String level;
        String color;

        if (condition == WeatherCondition.FOG_MIST) {
            level = "Low Visibility Warning";
            color = "#f59e0b";
            recommendation = "Dense fog/mist: Reduce driving speed, use low-beam fog lights, and double following distance on highways.";
        } else if (condition == WeatherCondition.SNOW_BLIZZARD) {
            level = "Icy Roads / Traction Risk";
            color = "#ef4444";
            recommendation = "Substantial winter hazards: Snow accumulation and black ice. Ensure winter tires or chains are fitted.";
        } else if (condition == WeatherCondition.HEAVY_RAIN || condition == WeatherCondition.THUNDERSTORM) {
            level = "Hydroplaning Alert";
            color = "#ef4444";
            recommendation = "Standing water on road surfaces: Beware of hydroplaning and sudden braking delays. Allow extra transit time.";
        } else if (windKmh > 55) {
            level = "High Crosswinds";
            color = "#f97316";
            recommendation = "Strong gusting winds: Hold steering wheel firmly, especially on bridges and open highway overpasses.";
        } else {
            level = "Normal Flow";
            color = "#10b981";
            recommendation = "Clear roadway conditions: Dry pavement and good visibility across arterial transit corridors.";
        }

        return new LifestyleAdvisory("Commute", title, recommendation, level, color, "car");
    }
}
