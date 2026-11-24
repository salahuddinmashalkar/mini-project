package com.aerocast.ui.util;

import com.aerocast.model.WeatherCondition;
import com.vaadin.flow.component.Html;

/**
 * Handcrafted vector SVG graphics renderer for meteorological conditions,
 * directional wind compass roses, atmospheric meters, and celestial bodies.
 * 100% pure Java without external asset dependencies.
 * Follows unslop design craft rules: unified line weights, zero emojis, exact telemetry.
 */
public final class SvgWeatherIcons {

    private SvgWeatherIcons() {}

    public static Html getConditionIcon(WeatherCondition condition, int size, boolean isDay) {
        return switch (condition) {
            case CLEAR_SUNNY -> isDay ? getSunnySvg(size) : getClearNightSvg(size);
            case PARTLY_CLOUDY -> isDay ? getPartlyCloudySvg(size) : getPartlyCloudyNightSvg(size);
            case CLOUDY_OVERCAST -> getOvercastSvg(size);
            case FOG_MIST -> getFogSvg(size);
            case LIGHT_RAIN -> getLightRainSvg(size);
            case HEAVY_RAIN -> getHeavyRainSvg(size);
            case THUNDERSTORM -> getThunderstormSvg(size);
            case SNOW_BLIZZARD -> getSnowSvg(size);
            case HIGH_HEAT -> getHighHeatSvg(size);
        };
    }

    public static Html getSunnySvg(int size) {
        return new Html(String.format("""
            <svg width="%d" height="%d" viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="32" cy="32" r="13" fill="#f59e0b"/>
              <g stroke="#f59e0b" stroke-width="2.5" stroke-linecap="round">
                <line x1="32" y1="8" x2="32" y2="13"/>
                <line x1="32" y1="51" x2="32" y2="56"/>
                <line x1="8" y1="32" x2="13" y2="32"/>
                <line x1="51" y1="32" x2="56" y2="32"/>
                <line x1="15" y1="15" x2="18.5" y2="18.5"/>
                <line x1="45.5" y1="45.5" x2="49" y2="49"/>
                <line x1="15" y1="49" x2="18.5" y2="45.5"/>
                <line x1="45.5" y1="18.5" x2="49" y2="15"/>
              </g>
            </svg>
            """, size, size));
    }

    public static Html getClearNightSvg(int size) {
        return new Html(String.format("""
            <svg width="%d" height="%d" viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M42 36C42 45.5 34 53 24 53C19 53 14.5 51 11 47.5C13 48 15 48.2 17 48.2C28 48.2 37 39.2 37 28.2C37 22.5 34.5 17.5 30.5 14C37 15.5 42 25 42 36Z" fill="#a3a9b8"/>
              <circle cx="46" cy="18" r="1.2" fill="#7a8296"/>
              <circle cx="50" cy="28" r="1" fill="#7a8296"/>
            </svg>
            """, size, size));
    }

    public static Html getPartlyCloudySvg(int size) {
        return new Html(String.format("""
            <svg width="%d" height="%d" viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="26" cy="24" r="10" fill="#f59e0b"/>
              <g stroke="#f59e0b" stroke-width="2" stroke-linecap="round">
                <line x1="26" y1="8" x2="26" y2="11"/>
                <line x1="14" y1="14" x2="16.5" y2="16.5"/>
                <line x1="9" y1="26" x2="12" y2="26"/>
                <line x1="38" y1="14" x2="35.5" y2="16.5"/>
              </g>
              <path d="M22 47H45C49 47 52 43.8 52 40C52 36.2 49 33 45.3 32.7C44.4 25.8 38.5 20.5 31.5 20.5C25.5 20.5 20.3 24.5 18.8 30C15 31 12 34.3 12 38.2C12 43 16 47 22 47Z" fill="#a3a9b8"/>
            </svg>
            """, size, size));
    }

    public static Html getPartlyCloudyNightSvg(int size) {
        return new Html(String.format("""
            <svg width="%d" height="%d" viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M34 26C34 32.5 28.5 38 21.5 38C17.5 38 14 36.5 11.5 34C13 34.5 14.5 34.8 16 34.8C24 34.8 30.5 28.2 30.5 20.2C30.5 16 28.8 12.2 26 9.5C31 11 34 18 34 26Z" fill="#a3a9b8"/>
              <path d="M24 50H45C49 50 52 46.8 52 43C52 39.2 49 36 45.3 35.7C44.4 29 38.5 24 31.5 24C25.5 24 20.3 28 18.8 33.5C15 34.5 12 37.8 12 41.7C12 46.5 16 50 24 50Z" fill="#7a8296"/>
            </svg>
            """, size, size));
    }

    public static Html getOvercastSvg(int size) {
        return new Html(String.format("""
            <svg width="%d" height="%d" viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M18 42H42C46 42 49 39 49 35.5C49 32 46 29 42.5 28.8C41.6 22.5 36.2 18 29.5 18C23.8 18 19 21.8 17.5 27C14 27.8 11 30.8 11 34.5C11 38.6 14.1 42 18 42Z" fill="#4b5565"/>
              <path d="M26 50H50C54 50 57 47 57 43.5C57 40 54 37 50.5 36.8C49.6 30.5 44.2 26 37.5 26C31.8 26 27 29.8 25.5 35C22 35.8 19 38.8 19 42.5C19 46.6 22.1 50 26 50Z" fill="#7a8296"/>
            </svg>
            """, size, size));
    }

    public static Html getLightRainSvg(int size) {
        return new Html(String.format("""
            <svg width="%d" height="%d" viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M22 40H46C50 40 53 37 53 33.5C53 30 50 27 46.5 26.8C45.6 20.5 40.2 16 33.5 16C27.8 16 23 19.8 21.5 25C18 25.8 15 28.8 15 32.5C15 36.6 18.1 40 22 40Z" fill="#7a8296"/>
              <g stroke="#38bdf8" stroke-width="2" stroke-linecap="round">
                <line x1="22" y1="46" x2="20" y2="52"/>
                <line x1="33" y1="46" x2="31" y2="52"/>
                <line x1="44" y1="46" x2="42" y2="52"/>
              </g>
            </svg>
            """, size, size));
    }

    public static Html getHeavyRainSvg(int size) {
        return new Html(String.format("""
            <svg width="%d" height="%d" viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M22 38H46C50 38 53 35 53 31.5C53 28 50 25 46.5 24.8C45.6 18.5 40.2 14 33.5 14C27.8 14 23 17.8 21.5 23C18 23.8 15 26.8 15 30.5C15 34.6 18.1 38 22 38Z" fill="#4b5565"/>
              <g stroke="#0284c7" stroke-width="2.5" stroke-linecap="round">
                <line x1="20" y1="44" x2="17" y2="54"/>
                <line x1="28" y1="44" x2="25" y2="54"/>
                <line x1="36" y1="44" x2="33" y2="54"/>
                <line x1="44" y1="44" x2="41" y2="54"/>
              </g>
            </svg>
            """, size, size));
    }

    public static Html getThunderstormSvg(int size) {
        return new Html(String.format("""
            <svg width="%d" height="%d" viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M22 36H46C50 36 53 33 53 29.5C53 26 50 23 46.5 22.8C45.6 16.5 40.2 12 33.5 12C27.8 12 23 15.8 21.5 21C18 21.8 15 24.8 15 28.5C15 32.6 18.1 36 22 36Z" fill="#2d3748"/>
              <polygon points="34,36 27,47 33,47 30,58 40,44 34,44" fill="#f59e0b"/>
            </svg>
            """, size, size));
    }

    public static Html getSnowSvg(int size) {
        return new Html(String.format("""
            <svg width="%d" height="%d" viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M22 38H46C50 38 53 35 53 31.5C53 28 50 25 46.5 24.8C45.6 18.5 40.2 14 33.5 14C27.8 14 23 17.8 21.5 23C18 23.8 15 26.8 15 30.5C15 34.6 18.1 38 22 38Z" fill="#7a8296"/>
              <g stroke="#38bdf8" stroke-width="1.8" stroke-linecap="round">
                <circle cx="22" cy="48" r="2" fill="#e0f2fe"/>
                <circle cx="34" cy="46" r="1.8" fill="#e0f2fe"/>
                <circle cx="44" cy="50" r="2" fill="#e0f2fe"/>
                <circle cx="28" cy="56" r="1.5" fill="#e0f2fe"/>
                <circle cx="38" cy="55" r="1.8" fill="#e0f2fe"/>
              </g>
            </svg>
            """, size, size));
    }

    public static Html getFogSvg(int size) {
        return new Html(String.format("""
            <svg width="%d" height="%d" viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
              <g stroke="#7a8296" stroke-width="2.5" stroke-linecap="round">
                <line x1="16" y1="22" x2="48" y2="22"/>
                <line x1="12" y1="30" x2="52" y2="30"/>
                <line x1="18" y1="38" x2="46" y2="38"/>
                <line x1="14" y1="46" x2="50" y2="46"/>
              </g>
            </svg>
            """, size, size));
    }

    public static Html getHighHeatSvg(int size) {
        return new Html(String.format("""
            <svg width="%d" height="%d" viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="32" cy="32" r="14" fill="#e5484d"/>
              <g stroke="#f59e0b" stroke-width="2.5" stroke-linecap="round">
                <line x1="32" y1="6" x2="32" y2="11"/>
                <line x1="32" y1="53" x2="32" y2="58"/>
                <line x1="6" y1="32" x2="11" y2="32"/>
                <line x1="53" y1="32" x2="58" y2="32"/>
              </g>
            </svg>
            """, size, size));
    }

    // Lucide-style clean line icons for telemetry cards (16x16 / 18x18, stroke 1.5, color #7a8296)

    public static Html getAqiIconSvg(int size) {
        return new Html(String.format("""
            <svg width="%d" height="%d" viewBox="0 0 24 24" fill="none" stroke="#7a8296" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <path d="M11 20A7 7 0 0 1 9.8 6.1C15.5 5 17 4.48 19 2c1 2 2 4.18 2 8 0 5.5-4.78 10-10 10Z"/>
              <path d="M2 21c0-3 1.85-5.36 5.08-6C9.5 14.52 12 13 13 12"/>
            </svg>
            """, size, size));
    }

    public static Html getUvIconSvg(int size) {
        return new Html(String.format("""
            <svg width="%d" height="%d" viewBox="0 0 24 24" fill="none" stroke="#7a8296" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="12" cy="12" r="4"/>
              <path d="M12 2v2"/>
              <path d="M12 20v2"/>
              <path d="m4.93 4.93 1.41 1.41"/>
              <path d="m17.66 17.66 1.41 1.41"/>
              <path d="M2 12h2"/>
              <path d="M20 12h2"/>
              <path d="m6.34 17.66-1.41 1.41"/>
              <path d="m19.07 4.93-1.41 1.41"/>
            </svg>
            """, size, size));
    }

    public static Html getWindIconSvg(int size) {
        return new Html(String.format("""
            <svg width="%d" height="%d" viewBox="0 0 24 24" fill="none" stroke="#7a8296" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <path d="M17.7 7.7a2.5 2.5 0 1 1 1.8 4.3H2"/>
              <path d="M9.6 4.6A2 2 0 1 1 11 8H2"/>
              <path d="M12.6 19.4A2 2 0 1 0 14 16H2"/>
            </svg>
            """, size, size));
    }

    public static Html getHumidityIconSvg(int size) {
        return new Html(String.format("""
            <svg width="%d" height="%d" viewBox="0 0 24 24" fill="none" stroke="#7a8296" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <path d="M12 22a7 7 0 0 0 7-7c0-2-1-3.9-3-6l-4-5-4 5c-2 2.1-3 4-3 6a7 7 0 0 0 7 7Z"/>
            </svg>
            """, size, size));
    }

    public static Html getVisibilityIconSvg(int size) {
        return new Html(String.format("""
            <svg width="%d" height="%d" viewBox="0 0 24 24" fill="none" stroke="#7a8296" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <path d="M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7Z"/>
              <circle cx="12" cy="12" r="3"/>
            </svg>
            """, size, size));
    }

    public static Html getPressureIconSvg(int size) {
        return new Html(String.format("""
            <svg width="%d" height="%d" viewBox="0 0 24 24" fill="none" stroke="#7a8296" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <path d="m12 14 4-4"/>
              <path d="M3.34 19a10 10 0 1 1 17.32 0"/>
            </svg>
            """, size, size));
    }

    public static Html getStationIconSvg(int size) {
        return new Html(String.format("""
            <svg width="%d" height="%d" viewBox="0 0 24 24" fill="none" stroke="#38bdf8" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="12" cy="12" r="2"/>
              <path d="M16.24 7.76a6 6 0 0 1 0 8.49"/>
              <path d="M7.76 16.24a6 6 0 0 1 0-8.49"/>
              <path d="M19.07 4.93a10 10 0 0 1 0 14.14"/>
              <path d="M4.93 19.07a10 10 0 0 1 0-14.14"/>
            </svg>
            """, size, size));
    }

    public static Html getWindCompassSvg(int degrees, int size) {
        return new Html(String.format("""
            <svg width="%d" height="%d" viewBox="0 0 64 64" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="32" cy="32" r="30" stroke="rgba(238,240,245,0.08)" stroke-width="1.5" fill="#11141c"/>
              <text x="32" y="12" fill="#a3a9b8" font-size="8" font-weight="500" text-anchor="middle" font-family="sans-serif">N</text>
              <text x="54" y="34.5" fill="#7a8296" font-size="7" font-weight="400" text-anchor="middle" font-family="sans-serif">E</text>
              <text x="32" y="56" fill="#7a8296" font-size="7" font-weight="400" text-anchor="middle" font-family="sans-serif">S</text>
              <text x="10" y="34.5" fill="#7a8296" font-size="7" font-weight="400" text-anchor="middle" font-family="sans-serif">W</text>
              <g transform="rotate(%d, 32, 32)">
                <polygon points="32,15 35,32 32,29 29,32" fill="#38bdf8"/>
                <polygon points="32,49 35,32 32,29 29,32" fill="#7a8296"/>
                <circle cx="32" cy="32" r="2.5" fill="#11141c" stroke="#38bdf8" stroke-width="1.2"/>
              </g>
            </svg>
            """, size, size, degrees));
    }

    public static Html getSolarArcSvg(double sunPositionPercent, String sunriseStr, String sunsetStr) {
        double sunX, sunY;
        boolean isNight = sunPositionPercent < 0;

        if (!isNight) {
            sunX = 100.0 - (75.0 * Math.cos(sunPositionPercent * Math.PI));
            sunY = 75.0 - (60.0 * Math.sin(sunPositionPercent * Math.PI));
        } else {
            sunX = 100.0;
            sunY = 86.0;
        }

        String sunMarker = isNight ?
                String.format("<circle cx='%.1f' cy='%.1f' r='4' fill='#4b5565' stroke='#7a8296' stroke-width='1.2'/>", sunX, sunY) :
                String.format("<circle cx='%.1f' cy='%.1f' r='5' fill='#f59e0b' stroke='#11141c' stroke-width='2'/>", sunX, sunY);

        return new Html(String.format("""
            <svg viewBox="0 0 200 95" width="100%%" height="95" xmlns="http://www.w3.org/2000/svg">
              <path d="M 25 75 A 75 60 0 0 1 175 75" fill="none" stroke="rgba(238,240,245,0.08)" stroke-width="1.5" stroke-dasharray="3 3"/>
              <line x1="15" y1="75" x2="185" y2="75" stroke="rgba(238,240,245,0.12)" stroke-width="1"/>
              %s
              <text x="25" y="90" fill="#7a8296" font-size="9" font-family="sans-serif">Sunrise %s</text>
              <text x="175" y="90" fill="#7a8296" font-size="9" font-family="sans-serif" text-anchor="end">Sunset %s</text>
            </svg>
            """, sunMarker, sunriseStr, sunsetStr));
    }

    public static Html getMoonPhaseSvg(int illuminationPercent, String phaseName, int size) {
        return new Html(String.format("""
            <svg width="%d" height="%d" viewBox="0 0 60 60" fill="none" xmlns="http://www.w3.org/2000/svg">
              <circle cx="30" cy="30" r="24" fill="#11141c" stroke="rgba(238,240,245,0.1)" stroke-width="1.2"/>
              <circle cx="30" cy="30" r="22" fill="#eef0f5" opacity="%.2f"/>
            </svg>
            """, size, size, Math.max(0.08, illuminationPercent / 100.0)));
    }

    public static Html getCurrentLocationIconSvg(int size) {
        return new Html(String.format("""
            <svg width="%d" height="%d" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="12" cy="12" r="3"/>
              <path d="M12 2v3"/>
              <path d="M12 19v3"/>
              <path d="M2 12h3"/>
              <path d="M19 12h3"/>
              <circle cx="12" cy="12" r="7" stroke-dasharray="2 2"/>
            </svg>
            """, size, size));
    }

    public static Html getGlobeIconSvg(int size) {
        return new Html(String.format("""
            <svg width="%d" height="%d" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5" stroke-linecap="round" stroke-linejoin="round">
              <circle cx="12" cy="12" r="10"/>
              <path d="M12 2a14.5 14.5 0 0 0 0 20 14.5 14.5 0 0 0 0-20"/>
              <path d="M2 12h20"/>
            </svg>
            """, size, size));
    }
}
