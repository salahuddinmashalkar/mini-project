package com.aerocast.model;

/**
 * Supported measurement unit systems.
 */
public enum UnitSystem {
    METRIC("°C", "km/h", "hPa", "km", "mm"),
    IMPERIAL("°F", "mph", "inHg", "mi", "in");

    private final String temperatureUnit;
    private final String speedUnit;
    private final String pressureUnit;
    private final String distanceUnit;
    private final String precipitationUnit;

    UnitSystem(String temperatureUnit, String speedUnit, String pressureUnit, String distanceUnit, String precipitationUnit) {
        this.temperatureUnit = temperatureUnit;
        this.speedUnit = speedUnit;
        this.pressureUnit = pressureUnit;
        this.distanceUnit = distanceUnit;
        this.precipitationUnit = precipitationUnit;
    }

    public String getTemperatureUnit() {
        return temperatureUnit;
    }

    public String getSpeedUnit() {
        return speedUnit;
    }

    public String getPressureUnit() {
        return pressureUnit;
    }

    public String getDistanceUnit() {
        return distanceUnit;
    }

    public String getPrecipitationUnit() {
        return precipitationUnit;
    }
}
