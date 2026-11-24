package com.aerocast.model;

import java.time.LocalTime;

/**
 * Solar arc and lunar phase astronomical metrics.
 */
public record AstronomyData(
        LocalTime sunrise,
        LocalTime sunset,
        String daylightDuration,
        String moonPhase,
        int moonIlluminationPercent,
        double sunPositionPercent, // 0.0 at sunrise, 0.5 at solar noon, 1.0 at sunset, -1 if nighttime
        boolean isSunUp
) {
    public static AstronomyData calculate(LocalTime sunrise, LocalTime sunset, LocalTime currentTime, String moonPhase, int moonIllum) {
        if (sunrise == null) sunrise = LocalTime.of(6, 15);
        if (sunset == null) sunset = LocalTime.of(18, 45);
        if (currentTime == null) currentTime = LocalTime.now();

        long totalDayMinutes = java.time.Duration.between(sunrise, sunset).toMinutes();
        if (totalDayMinutes <= 0) totalDayMinutes = 12 * 60;

        String daylight = String.format("%dh %02dm", totalDayMinutes / 60, totalDayMinutes % 60);

        boolean isUp = !currentTime.isBefore(sunrise) && !currentTime.isAfter(sunset);
        double position;

        if (isUp) {
            long currentMinutes = java.time.Duration.between(sunrise, currentTime).toMinutes();
            position = Math.max(0.0, Math.min(1.0, (double) currentMinutes / totalDayMinutes));
        } else {
            position = -1.0;
        }

        return new AstronomyData(sunrise, sunset, daylight, moonPhase, moonIllum, position, isUp);
    }
}
