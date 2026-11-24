package com.aerocast.model;

/**
 * Atmospheric Air Quality Index (AQI) telemetry metrics.
 */
public record AirQualityMetrics(
        int aqiUs,
        double pm25,
        double pm10,
        double ozone,
        double nitrogenDioxide,
        double sulphurDioxide,
        String category,
        String healthRecommendation,
        String badgeColor
) {
    public static AirQualityMetrics of(int aqiUs, double pm25, double pm10, double ozone, double no2, double so2) {
        String category;
        String rec;
        String color;

        if (aqiUs <= 50) {
            category = "Good";
            rec = "Air quality is ideal for outdoor activities.";
            color = "#10b981"; // emerald-500
        } else if (aqiUs <= 100) {
            category = "Moderate";
            rec = "Acceptable quality; unusually sensitive individuals should monitor symptoms.";
            color = "#f59e0b"; // amber-500
        } else if (aqiUs <= 150) {
            category = "Unhealthy for Sensitive Groups";
            rec = "Sensitive groups may experience health effects; limit prolonged outdoor exertion.";
            color = "#f97316"; // orange-500
        } else if (aqiUs <= 200) {
            category = "Unhealthy";
            rec = "Everyone may begin to experience health effects; wear an N95 mask outside.";
            color = "#ef4444"; // red-500
        } else if (aqiUs <= 300) {
            category = "Very Unhealthy";
            rec = "Health alert: serious risk. Avoid outdoor activity and use air purifiers.";
            color = "#a855f7"; // purple-500
        } else {
            category = "Hazardous";
            rec = "Health warnings of emergency conditions. Keep windows closed and stay indoors.";
            color = "#881337"; // rose-900
        }

        return new AirQualityMetrics(aqiUs, pm25, pm10, ozone, no2, so2, category, rec, color);
    }
}
