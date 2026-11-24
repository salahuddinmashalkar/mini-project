package com.aerocast.ui.util;

import com.aerocast.model.WeatherCondition;

/**
 * Design tokens and surface styling following the unslop design craft rules.
 * Implements a strict Ink-neutral ramp with quiet hairlines, deliberate semantic accents,
 * and zero AI-slop glows or loud gradients.
 */
public final class UiThemeUtils {

    private UiThemeUtils() {}

    // Ink Dark Neutral Ramp
    public static final String COLOR_BG_PAGE = "#0b0d14";
    public static final String COLOR_SURFACE_CARD = "#11141c";
    public static final String COLOR_SURFACE_RAISED = "#1a1e28";
    public static final String COLOR_HAIRLINE = "rgba(238, 240, 245, 0.08)";
    public static final String COLOR_HAIRLINE_SUBTLE = "rgba(238, 240, 245, 0.05)";

    // Three clean text roles
    public static final String COLOR_TEXT_PRIMARY = "#eef0f5";
    public static final String COLOR_TEXT_SECONDARY = "#a3a9b8";
    public static final String COLOR_TEXT_MUTED = "#7a8296";

    // Deliberate Accents
    public static final String COLOR_ACCENT = "#38bdf8"; // Sky/observatory cyan
    public static final String COLOR_ACCENT_MUTED = "rgba(56, 189, 248, 0.12)";
    public static final String COLOR_POSITIVE = "#4cc38a";
    public static final String COLOR_WARNING = "#f59e0b";
    public static final String COLOR_NEGATIVE = "#e5484d";

    // Standard card styling: clean surface step with quiet hairline and subtle top highlight
    public static final String CARD_STYLE =
            "background: " + COLOR_SURFACE_CARD + "; " +
            "border: 1px solid " + COLOR_HAIRLINE + "; " +
            "border-radius: 16px; " +
            "box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.04);";

    /**
     * Hero card background: refined subtle tonal shift staying within the Ink neutral ramp,
     * without neon washes or noisy gradients.
     */
    public static String getHeroGradient(WeatherCondition condition, boolean isDay) {
        if (!isDay) {
            return "linear-gradient(180deg, #131722 0%, #11141c 100%)";
        }
        return switch (condition) {
            case CLEAR_SUNNY -> "linear-gradient(180deg, #171b26 0%, #11141c 100%)";
            case PARTLY_CLOUDY, CLOUDY_OVERCAST -> "linear-gradient(180deg, #141720 0%, #11141c 100%)";
            case LIGHT_RAIN, HEAVY_RAIN -> "linear-gradient(180deg, #121824 0%, #11141c 100%)";
            case SNOW_BLIZZARD -> "linear-gradient(180deg, #141a26 0%, #11141c 100%)";
            case THUNDERSTORM -> "linear-gradient(180deg, #151624 0%, #11141c 100%)";
            case FOG_MIST, HIGH_HEAT -> "linear-gradient(180deg, #161922 0%, #11141c 100%)";
        };
    }

    /**
     * Hairline accent tone: subtle 12-16% opacity tone reflecting condition without glowing neon.
     */
    public static String getHeroBorderColor(WeatherCondition condition) {
        return switch (condition) {
            case CLEAR_SUNNY -> "rgba(245, 158, 11, 0.22)";
            case LIGHT_RAIN, HEAVY_RAIN, SNOW_BLIZZARD -> "rgba(56, 189, 248, 0.22)";
            case THUNDERSTORM -> "rgba(168, 85, 247, 0.22)";
            case HIGH_HEAT -> "rgba(239, 68, 68, 0.22)";
            default -> COLOR_HAIRLINE;
        };
    }
}
