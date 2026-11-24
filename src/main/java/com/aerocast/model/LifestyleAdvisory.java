package com.aerocast.model;

/**
 * Lifestyle recommendations based on real-time meteorological conditions.
 */
public record LifestyleAdvisory(
        String category,
        String title,
        String recommendation,
        String badgeLevel,
        String badgeColor,
        String iconKey
) {}
