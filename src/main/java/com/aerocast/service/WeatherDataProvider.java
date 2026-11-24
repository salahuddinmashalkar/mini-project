package com.aerocast.service;

import com.aerocast.model.GeoLocation;
import com.aerocast.model.WeatherReport;

import java.util.List;

/**
 * Strategy interface for weather information retrieval.
 * Supports retrieval by geographic query or direct geographic coordinates.
 */
public interface WeatherDataProvider {

    /**
     * Retrieve complete weather report for a given city and country.
     */
    WeatherReport getWeather(String cityName, String country);

    /**
     * Retrieve complete weather report directly by geographic latitude/longitude.
     */
    default WeatherReport getWeatherByCoordinates(double latitude, double longitude, String cityName, String country) {
        return getWeather(cityName, country);
    }

    /**
     * Search and geocode matching cities.
     */
    List<GeoLocation> searchCities(String query);

    /**
     * Identifier of the provider implementation.
     */
    String getProviderName();

    /**
     * Indicates whether this provider makes live external network calls.
     */
    boolean isLiveApi();
}
