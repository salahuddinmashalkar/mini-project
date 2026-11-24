package com.aerocast.service;

import com.aerocast.model.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.*;

/**
 * High-fidelity offline sample meteorological engine covering 8 distinct global climatic regions.
 */
@Service
public class SampleWeatherService implements WeatherDataProvider {

    private final AdvisoryEngineService advisoryEngineService;
    private final LocationService locationService;

    @org.springframework.beans.factory.annotation.Autowired
    public SampleWeatherService(AdvisoryEngineService advisoryEngineService, LocationService locationService) {
        this.advisoryEngineService = advisoryEngineService;
        this.locationService = locationService;
    }

    private static final Map<String, CityProfile> PROFILES = new LinkedHashMap<>();

    record CityProfile(
            String name,
            String country,
            double lat,
            double lon,
            double currentTemp,
            double feelsLike,
            double tempMin,
            double tempMax,
            WeatherCondition condition,
            String desc,
            int humidity,
            double dewPoint,
            double windSpeed,
            int windDeg,
            String windCard,
            double pressure,
            double visibility,
            int uv,
            int aqi,
            double pm25,
            double pm10,
            double ozone,
            LocalTime sunrise,
            LocalTime sunset,
            String moonPhase,
            int moonIllum
    ) {}

    static {
        // 1. Tokyo - Clear & Sunny
        PROFILES.put("tokyo", new CityProfile(
                "Tokyo", "Japan", 35.6762, 139.6503,
                21.4, 21.0, 15.0, 23.5,
                WeatherCondition.CLEAR_SUNNY, "Gentle sunny skies with high visibility",
                48, 10.2, 12.5, 135, "SE",
                1018.0, 18.5, 6,
                32, 7.8, 14.2, 45.0,
                LocalTime.of(6, 12), LocalTime.of(17, 48), "Waxing Gibbous", 78
        ));

        // 2. London - Light Rain & Overcast
        PROFILES.put("london", new CityProfile(
                "London", "United Kingdom", 51.5074, -0.1278,
                12.2, 10.5, 8.0, 14.0,
                WeatherCondition.LIGHT_RAIN, "Intermittent drizzle and low stratocumulus clouds",
                86, 9.8, 19.4, 240, "WSW",
                1009.2, 8.0, 2,
                38, 9.4, 16.1, 32.0,
                LocalTime.of(7, 24), LocalTime.of(16, 18), "Full Moon", 99
        ));

        // 3. New York - Partly Cloudy
        PROFILES.put("new york", new CityProfile(
                "New York", "United States", 40.7128, -74.0060,
                16.8, 15.5, 11.2, 19.0,
                WeatherCondition.PARTLY_CLOUDY, "Passing cirrus clouds with brisk autumn breeze",
                55, 7.8, 22.0, 310, "NW",
                1014.8, 16.0, 4,
                48, 11.8, 21.4, 41.2,
                LocalTime.of(6, 45), LocalTime.of(16, 52), "Waning Gibbous", 84
        ));

        // 4. Mumbai - Heavy Rain & Monsoon
        PROFILES.put("mumbai", new CityProfile(
                "Mumbai", "India", 19.0760, 72.8777,
                28.6, 33.4, 25.0, 30.5,
                WeatherCondition.HEAVY_RAIN, "Monsoonal downpour with localized squalls",
                93, 27.2, 28.5, 225, "SW",
                1002.5, 4.2, 3,
                128, 48.2, 88.5, 28.0,
                LocalTime.of(6, 40), LocalTime.of(18, 02), "First Quarter", 52
        ));

        // 5. Paris - Fog & Mist
        PROFILES.put("paris", new CityProfile(
                "Paris", "France", 48.8566, 2.3522,
                9.1, 7.4, 5.5, 11.8,
                WeatherCondition.FOG_MIST, "Dense radiation fog lifting into low stratus",
                95, 8.3, 8.5, 45, "NE",
                1022.0, 2.8, 1,
                42, 10.5, 18.0, 34.0,
                LocalTime.of(8, 05), LocalTime.of(17, 10), "Waxing Crescent", 32
        ));

        // 6. Sydney - High Heat
        PROFILES.put("sydney", new CityProfile(
                "Sydney", "Australia", -33.8688, 151.2093,
                35.2, 37.8, 22.5, 36.8,
                WeatherCondition.HIGH_HEAT, "Intense solar heating with gusty hot westerly winds",
                32, 15.6, 34.2, 280, "W",
                1006.4, 22.0, 11,
                58, 14.5, 26.2, 62.0,
                LocalTime.of(5, 40), LocalTime.of(19, 45), "Waxing Crescent", 21
        ));

        // 7. Reykjavik - Snow & Blizzard
        PROFILES.put("reykjavik", new CityProfile(
                "Reykjavik", "Iceland", 64.1466, -21.9426,
                -3.8, -10.2, -6.5, -1.0,
                WeatherCondition.SNOW_BLIZZARD, "Blowing snow with sub-zero gale force gusts",
                88, -5.4, 46.8, 30, "NNE",
                994.0, 1.5, 0,
                14, 2.8, 5.1, 22.0,
                LocalTime.of(9, 55), LocalTime.of(16, 05), "New Moon", 4
        ));

        // 8. Cairo - High Desert Heat
        PROFILES.put("cairo", new CityProfile(
                "Cairo", "Egypt", 30.0444, 31.2357,
                32.5, 31.8, 20.0, 34.0,
                WeatherCondition.CLEAR_SUNNY, "Unobstructed desert sunlight and arid atmosphere",
                26, 10.1, 15.0, 360, "N",
                1012.0, 15.0, 9,
                92, 31.5, 68.0, 58.5,
                LocalTime.of(6, 18), LocalTime.of(17, 04), "Waning Crescent", 18
        ));
    }

    @Override
    public WeatherReport getWeatherByCoordinates(double latitude, double longitude, String cityName, String country) {
        String key = (cityName != null) ? cityName.trim().toLowerCase() : "";
        if (!key.isEmpty() && PROFILES.containsKey(key)) {
            return getWeather(cityName, country);
        }
        CityProfile closest = PROFILES.values().stream()
                .min(Comparator.comparingDouble(p -> Math.hypot(p.lat() - latitude, p.lon() - longitude)))
                .orElse(PROFILES.get("tokyo"));
        return getWeather(cityName != null && !cityName.isBlank() ? cityName : closest.name(),
                country != null && !country.isBlank() ? country : closest.country());
    }

    @Override
    public WeatherReport getWeather(String cityName, String country) {
        String key = (cityName != null) ? cityName.trim().toLowerCase() : "tokyo";
        CityProfile p = PROFILES.getOrDefault(key, PROFILES.get("tokyo"));

        // If city not directly matched in the 8 baseline profiles, locate it via LocationService
        if (!PROFILES.containsKey(key)) {
            double resolvedLat = p.lat();
            double resolvedLon = p.lon();
            String resolvedCountry = country != null && !country.isBlank() ? country : "Global";

            if (locationService != null) {
                List<GeoLocation> matches = locationService.searchLocations(cityName);
                if (!matches.isEmpty()) {
                    GeoLocation loc = matches.get(0);
                    resolvedLat = loc.latitude();
                    resolvedLon = loc.longitude();
                    if (loc.country() != null && !loc.country().isBlank()) {
                        resolvedCountry = loc.country();
                    }
                }
            }

            p = new CityProfile(
                    cityName != null ? cityName : "Tokyo",
                    resolvedCountry,
                    resolvedLat, resolvedLon, p.currentTemp(), p.feelsLike(), p.tempMin(), p.tempMax(),
                    p.condition(), p.desc(), p.humidity(), p.dewPoint(), p.windSpeed(),
                    p.windDeg(), p.windCard(), p.pressure(), p.visibility(), p.uv(),
                    p.aqi(), p.pm25(), p.pm10(), p.ozone(), p.sunrise(), p.sunset(),
                    p.moonPhase(), p.moonIllum()
            );
        }

        LocalDateTime now = LocalDateTime.now();
        AirQualityMetrics aqi = AirQualityMetrics.of(p.aqi(), p.pm25(), p.pm10(), p.ozone(), 18.2, 5.4);
        AstronomyData astronomy = AstronomyData.calculate(p.sunrise(), p.sunset(), now.toLocalTime(), p.moonPhase(), p.moonIllum());

        List<HourlyForecast> hourly = generateHourlyForecast(now, p);
        List<DailyForecast> daily = generateDailyForecast(now.toLocalDate(), p);
        List<LifestyleAdvisory> advisories = advisoryEngineService.generateAdvisories(
                p.currentTemp(), p.condition(), p.humidity(), p.windSpeed(), p.uv(), aqi
        );

        String sampleSourceDesc = "Predefined Offline Dataset (" + p.name() + " meteorological profile)";

        return new WeatherReport(
                p.name(),
                p.country(),
                p.lat(),
                p.lon(),
                now,
                p.currentTemp(),
                p.feelsLike(),
                p.tempMin(),
                p.tempMax(),
                p.condition(),
                p.desc(),
                p.humidity(),
                p.dewPoint(),
                p.windSpeed(),
                p.windDeg(),
                p.windCard(),
                p.pressure(),
                p.visibility(),
                p.uv(),
                aqi,
                astronomy,
                hourly,
                daily,
                advisories,
                false,
                sampleSourceDesc,
                "Simulated atmospheric profile active. All metrics calculated offline."
        );
    }

    @Override
    public List<GeoLocation> searchCities(String query) {
        if (locationService != null) {
            return locationService.searchLocations(query);
        }
        if (query == null || query.isBlank()) {
            return PROFILES.values().stream()
                    .map(p -> new GeoLocation(p.name(), p.country(), "", p.lat(), p.lon(), "UTC"))
                    .toList();
        }
        String q = query.toLowerCase().trim();
        return PROFILES.values().stream()
                .filter(p -> p.name().toLowerCase().contains(q) || p.country().toLowerCase().contains(q))
                .map(p -> new GeoLocation(p.name(), p.country(), "", p.lat(), p.lon(), "UTC"))
                .toList();
    }

    @Override
    public String getProviderName() {
        return "Sample Data Mode (Offline 8-City Profiles)";
    }

    @Override
    public boolean isLiveApi() {
        return false;
    }

    private List<HourlyForecast> generateHourlyForecast(LocalDateTime start, CityProfile p) {
        List<HourlyForecast> list = new ArrayList<>();
        double baseTemp = p.currentTemp();
        int baseHumidity = p.humidity();
        double baseWind = p.windSpeed();

        for (int i = 0; i < 24; i++) {
            LocalDateTime time = start.plusHours(i).withMinute(0).withSecond(0);
            int hour = time.getHour();
            boolean isDay = hour >= 6 && hour < 20;

            // Diurnal temperature fluctuation
            double tempOffset = Math.sin((hour - 9) * Math.PI / 12.0) * 3.5;
            double hourTemp = Math.round((baseTemp + tempOffset) * 10.0) / 10.0;
            double apparent = Math.round((hourTemp + (baseTemp > 24 ? 1.5 : -1.0)) * 10.0) / 10.0;

            int precipProb = switch (p.condition()) {
                case HEAVY_RAIN -> Math.min(95, 75 + (i % 20));
                case LIGHT_RAIN -> Math.min(80, 50 + (i % 25));
                case THUNDERSTORM -> Math.min(90, 60 + (i % 30));
                case SNOW_BLIZZARD -> Math.min(85, 65 + (i % 15));
                case PARTLY_CLOUDY -> Math.max(10, (i * 3) % 35);
                default -> Math.max(5, (i * 2) % 15);
            };

            double precipMm = precipProb > 50 ? (precipProb / 20.0) : 0.0;
            int uv = isDay ? Math.max(0, (int) Math.round(p.uv() * Math.sin((hour - 6) * Math.PI / 14.0))) : 0;

            list.add(new HourlyForecast(
                    time,
                    hourTemp,
                    apparent,
                    precipProb,
                    Math.round(precipMm * 10.0) / 10.0,
                    p.condition(),
                    Math.max(15, Math.min(99, baseHumidity + (isDay ? -8 : 10))),
                    Math.round((baseWind + ((i % 5) - 2)) * 10.0) / 10.0,
                    uv,
                    isDay
            ));
        }
        return list;
    }

    private List<DailyForecast> generateDailyForecast(LocalDate start, CityProfile p) {
        List<DailyForecast> list = new ArrayList<>();
        WeatherCondition[] sequence = getConditionSequence(p.condition());

        for (int i = 0; i < 7; i++) {
            LocalDate date = start.plusDays(i);
            String dayName = (i == 0) ? "Today" : date.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            WeatherCondition cond = sequence[i % sequence.length];

            double dayDelta = Math.sin(i * 0.8) * 2.0;
            double min = Math.round((p.tempMin() + dayDelta) * 10.0) / 10.0;
            double max = Math.round((p.tempMax() + dayDelta) * 10.0) / 10.0;
            if (min > max) {
                double temp = min;
                min = max - 3.0;
                max = temp;
            }

            int prob = switch (cond) {
                case HEAVY_RAIN, THUNDERSTORM -> 85;
                case LIGHT_RAIN -> 65;
                case SNOW_BLIZZARD -> 80;
                case PARTLY_CLOUDY -> 30;
                case FOG_MIST -> 25;
                default -> 10;
            };

            double rainMm = prob > 50 ? 8.5 : 0.0;

            list.add(new DailyForecast(
                    date,
                    dayName,
                    min,
                    max,
                    cond,
                    prob,
                    rainMm,
                    p.windSpeed(),
                    p.uv()
            ));
        }
        return list;
    }

    private WeatherCondition[] getConditionSequence(WeatherCondition current) {
        return switch (current) {
            case CLEAR_SUNNY -> new WeatherCondition[]{WeatherCondition.CLEAR_SUNNY, WeatherCondition.CLEAR_SUNNY, WeatherCondition.PARTLY_CLOUDY, WeatherCondition.CLEAR_SUNNY, WeatherCondition.PARTLY_CLOUDY, WeatherCondition.LIGHT_RAIN, WeatherCondition.CLEAR_SUNNY};
            case HEAVY_RAIN -> new WeatherCondition[]{WeatherCondition.HEAVY_RAIN, WeatherCondition.THUNDERSTORM, WeatherCondition.LIGHT_RAIN, WeatherCondition.PARTLY_CLOUDY, WeatherCondition.LIGHT_RAIN, WeatherCondition.CLOUDY_OVERCAST, WeatherCondition.PARTLY_CLOUDY};
            case SNOW_BLIZZARD -> new WeatherCondition[]{WeatherCondition.SNOW_BLIZZARD, WeatherCondition.SNOW_BLIZZARD, WeatherCondition.CLOUDY_OVERCAST, WeatherCondition.PARTLY_CLOUDY, WeatherCondition.SNOW_BLIZZARD, WeatherCondition.CLOUDY_OVERCAST, WeatherCondition.CLEAR_SUNNY};
            case LIGHT_RAIN -> new WeatherCondition[]{WeatherCondition.LIGHT_RAIN, WeatherCondition.CLOUDY_OVERCAST, WeatherCondition.PARTLY_CLOUDY, WeatherCondition.LIGHT_RAIN, WeatherCondition.CLEAR_SUNNY, WeatherCondition.PARTLY_CLOUDY, WeatherCondition.LIGHT_RAIN};
            case FOG_MIST -> new WeatherCondition[]{WeatherCondition.FOG_MIST, WeatherCondition.PARTLY_CLOUDY, WeatherCondition.CLEAR_SUNNY, WeatherCondition.FOG_MIST, WeatherCondition.CLOUDY_OVERCAST, WeatherCondition.LIGHT_RAIN, WeatherCondition.CLEAR_SUNNY};
            default -> new WeatherCondition[]{current, WeatherCondition.PARTLY_CLOUDY, WeatherCondition.CLEAR_SUNNY, WeatherCondition.LIGHT_RAIN, WeatherCondition.PARTLY_CLOUDY, WeatherCondition.CLEAR_SUNNY, current};
        };
    }
}
