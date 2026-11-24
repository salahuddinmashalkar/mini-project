package com.aerocast.service;

import com.aerocast.model.FavoriteCityEntity;
import com.aerocast.model.UnitSystem;
import com.aerocast.model.UserPreferenceEntity;
import com.aerocast.repository.FavoriteCityRepository;
import com.aerocast.repository.UserPreferenceRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service managing user settings, favorite cities, and API credentials.
 * Automatically resolves credentials from .env / environment variables as base defaults,
 * with H2 database preferences taking precedence if updated by the user in the UI.
 */
@Service
public class UserPreferenceService {

    public static final String KEY_UNIT_SYSTEM = "pref.unit_system";
    public static final String KEY_GROQ_API_KEY = "pref.groq_api_key";
    public static final String KEY_OPENWEATHER_API_KEY = "pref.openweather_api_key";
    public static final String KEY_DEFAULT_CITY = "pref.default_city";
    public static final String KEY_DEFAULT_COUNTRY = "pref.default_country";

    private final UserPreferenceRepository preferenceRepository;
    private final FavoriteCityRepository favoriteCityRepository;

    @Value("${aerocast.groq-api-key:}")
    private String envGroqApiKey;

    @Value("${aerocast.openweather-api-key:}")
    private String envOpenWeatherApiKey;

    @Value("${aerocast.default-city:Tokyo}")
    private String defaultCity;

    @Value("${aerocast.default-country:Japan}")
    private String defaultCountry;

    @Value("${aerocast.default-unit:METRIC}")
    private String defaultUnit;

    public UserPreferenceService(UserPreferenceRepository preferenceRepository,
                                 FavoriteCityRepository favoriteCityRepository) {
        this.preferenceRepository = preferenceRepository;
        this.favoriteCityRepository = favoriteCityRepository;
    }

    @PostConstruct
    @Transactional
    public void initDefaults() {
        if (favoriteCityRepository.count() == 0) {
            // Seed curated initial global cities
            favoriteCityRepository.save(new FavoriteCityEntity("Tokyo", "Japan", 35.6762, 139.6503, 1));
            favoriteCityRepository.save(new FavoriteCityEntity("London", "United Kingdom", 51.5074, -0.1278, 2));
            favoriteCityRepository.save(new FavoriteCityEntity("New York", "United States", 40.7128, -74.0060, 3));
            favoriteCityRepository.save(new FavoriteCityEntity("Mumbai", "India", 19.0760, 72.8777, 4));
            favoriteCityRepository.save(new FavoriteCityEntity("Reykjavik", "Iceland", 64.1466, -21.9426, 5));
        }

        if (preferenceRepository.findById(KEY_UNIT_SYSTEM).isEmpty()) {
            UnitSystem initialUnit = UnitSystem.METRIC;
            try {
                initialUnit = UnitSystem.valueOf(defaultUnit.toUpperCase());
            } catch (Exception ignored) {}
            preferenceRepository.save(new UserPreferenceEntity(KEY_UNIT_SYSTEM, initialUnit.name()));
        }

        if (preferenceRepository.findById(KEY_DEFAULT_CITY).isEmpty()) {
            preferenceRepository.save(new UserPreferenceEntity(KEY_DEFAULT_CITY, defaultCity));
            preferenceRepository.save(new UserPreferenceEntity(KEY_DEFAULT_COUNTRY, defaultCountry));
        }
    }

    public UnitSystem getUnitSystem() {
        return preferenceRepository.findById(KEY_UNIT_SYSTEM)
                .map(p -> UnitSystem.valueOf(p.getPrefValue()))
                .orElse(UnitSystem.METRIC);
    }

    @Transactional
    public void setUnitSystem(UnitSystem unitSystem) {
        preferenceRepository.save(new UserPreferenceEntity(KEY_UNIT_SYSTEM, unitSystem.name()));
    }

    /**
     * Resolves Groq API key: checks database first, then falls back to .env / environment variable.
     */
    public Optional<String> getGroqApiKey() {
        Optional<String> dbKey = preferenceRepository.findById(KEY_GROQ_API_KEY)
                .map(UserPreferenceEntity::getPrefValue)
                .filter(val -> !val.isBlank());

        if (dbKey.isPresent()) {
            return dbKey;
        }

        if (envGroqApiKey != null && !envGroqApiKey.isBlank()) {
            return Optional.of(envGroqApiKey.trim());
        }

        return Optional.empty();
    }

    @Transactional
    public void setGroqApiKey(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            preferenceRepository.deleteById(KEY_GROQ_API_KEY);
        } else {
            preferenceRepository.save(new UserPreferenceEntity(KEY_GROQ_API_KEY, apiKey.trim()));
        }
    }

    /**
     * Resolves OpenWeatherMap API key: checks database first, then falls back to .env / environment variable.
     */
    public Optional<String> getOpenWeatherApiKey() {
        Optional<String> dbKey = preferenceRepository.findById(KEY_OPENWEATHER_API_KEY)
                .map(UserPreferenceEntity::getPrefValue)
                .filter(val -> !val.isBlank());

        if (dbKey.isPresent()) {
            return dbKey;
        }

        if (envOpenWeatherApiKey != null && !envOpenWeatherApiKey.isBlank()) {
            return Optional.of(envOpenWeatherApiKey.trim());
        }

        return Optional.empty();
    }

    @Transactional
    public void setOpenWeatherApiKey(String apiKey) {
        if (apiKey == null || apiKey.isBlank()) {
            preferenceRepository.deleteById(KEY_OPENWEATHER_API_KEY);
        } else {
            preferenceRepository.save(new UserPreferenceEntity(KEY_OPENWEATHER_API_KEY, apiKey.trim()));
        }
    }

    public List<FavoriteCityEntity> getFavoriteCities() {
        return favoriteCityRepository.findAllByOrderByDisplayOrderAscPinnedAtAsc();
    }

    public boolean isCityFavorited(String cityName) {
        return favoriteCityRepository.existsByCityNameIgnoreCase(cityName);
    }

    @Transactional
    public FavoriteCityEntity addFavoriteCity(String cityName, String country, double lat, double lon) {
        if (favoriteCityRepository.existsByCityNameIgnoreCase(cityName)) {
            return favoriteCityRepository.findByCityNameIgnoreCase(cityName).orElse(null);
        }
        int nextOrder = (int) favoriteCityRepository.count() + 1;
        FavoriteCityEntity entity = new FavoriteCityEntity(cityName, country, lat, lon, nextOrder);
        return favoriteCityRepository.save(entity);
    }

    @Transactional
    public void removeFavoriteCity(String cityName) {
        favoriteCityRepository.deleteByCityNameIgnoreCase(cityName);
    }
}
