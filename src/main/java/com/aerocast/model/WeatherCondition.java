package com.aerocast.model;

/**
 * Weather condition classification covering realistic meteorological states.
 */
public enum WeatherCondition {
    CLEAR_SUNNY("Clear & Sunny", "sunny", "#f59e0b", "#d97706"),
    PARTLY_CLOUDY("Partly Cloudy", "partly-cloudy", "#64748b", "#475569"),
    CLOUDY_OVERCAST("Overcast", "cloudy", "#475569", "#334155"),
    FOG_MIST("Fog & Mist", "fog", "#94a3b8", "#64748b"),
    LIGHT_RAIN("Light Rain", "light-rain", "#38bdf8", "#0284c7"),
    HEAVY_RAIN("Heavy Rain", "heavy-rain", "#0284c7", "#0369a1"),
    THUNDERSTORM("Severe Thunderstorm", "thunderstorm", "#a855f7", "#7e22ce"),
    SNOW_BLIZZARD("Snow & Blizzard", "snow", "#38bdf8", "#0ea5e9"),
    HIGH_HEAT("Extreme Heatwave", "high-heat", "#ef4444", "#b91c1c");

    private final String displayName;
    private final String iconType;
    private final String primaryColor;
    private final String secondaryColor;

    WeatherCondition(String displayName, String iconType, String primaryColor, String secondaryColor) {
        this.displayName = displayName;
        this.iconType = iconType;
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getIconType() {
        return iconType;
    }

    public String getPrimaryColor() {
        return primaryColor;
    }

    public String getSecondaryColor() {
        return secondaryColor;
    }

    /**
     * Map WMO Weather Interpretation Codes (used by Open-Meteo) to standard WeatherCondition.
     */
    public static WeatherCondition fromWmoCode(int code, boolean isDay) {
        return switch (code) {
            case 0, 1 -> CLEAR_SUNNY;
            case 2 -> PARTLY_CLOUDY;
            case 3 -> CLOUDY_OVERCAST;
            case 45, 48 -> FOG_MIST;
            case 51, 53, 55, 61, 80 -> LIGHT_RAIN;
            case 63, 65, 81, 82 -> HEAVY_RAIN;
            case 71, 73, 75, 77, 85, 86 -> SNOW_BLIZZARD;
            case 95, 96, 99 -> THUNDERSTORM;
            default -> isDay ? CLEAR_SUNNY : PARTLY_CLOUDY;
        };
    }
}
