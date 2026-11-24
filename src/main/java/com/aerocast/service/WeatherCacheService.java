package com.aerocast.service;

import com.aerocast.model.WeatherReport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * High-performance in-memory TTL snapshot cache for meteorological payloads.
 * Protects against redundant network requests and API rate limits.
 */
@Service
public class WeatherCacheService {

    private record CachedEntry(WeatherReport report, Instant cachedAt) {}

    private final Map<String, CachedEntry> cache = new ConcurrentHashMap<>();
    private final Duration ttl;

    public WeatherCacheService(@Value("${aerocast.cache-duration-minutes:15}") int cacheMinutes) {
        this.ttl = Duration.ofMinutes(cacheMinutes);
    }

    public Optional<WeatherReport> get(String cityName, boolean isLiveApi) {
        if (cityName == null || cityName.isBlank()) {
            return Optional.empty();
        }
        String key = buildKey(cityName, isLiveApi);
        CachedEntry entry = cache.get(key);
        if (entry != null) {
            if (Duration.between(entry.cachedAt(), Instant.now()).compareTo(ttl) < 0) {
                return Optional.of(entry.report());
            } else {
                cache.remove(key);
            }
        }
        return Optional.empty();
    }

    public void put(String cityName, boolean isLiveApi, WeatherReport report) {
        if (cityName == null || cityName.isBlank() || report == null) {
            return;
        }
        String key = buildKey(cityName, isLiveApi);
        cache.put(key, new CachedEntry(report, Instant.now()));
    }

    public void evict(String cityName) {
        if (cityName != null) {
            cache.remove(buildKey(cityName, true));
            cache.remove(buildKey(cityName, false));
        }
    }

    public void clear() {
        cache.clear();
    }

    private String buildKey(String cityName, boolean isLiveApi) {
        return (cityName.trim().toLowerCase()) + "::" + (isLiveApi ? "live" : "sample");
    }
}
