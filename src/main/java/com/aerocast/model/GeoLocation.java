package com.aerocast.model;

/**
 * Geographic coordinates and administrative location descriptor.
 * Supports full administrative hierarchy (village/city, state/district, country).
 */
public record GeoLocation(
        String cityName,
        String country,
        String admin1,
        double latitude,
        double longitude,
        String timezone
) {
    public String getDisplayName() {
        StringBuilder sb = new StringBuilder(cityName != null && !cityName.isBlank() ? cityName : "Location");
        if (admin1 != null && !admin1.isBlank() && !admin1.equalsIgnoreCase(cityName)) {
            sb.append(", ").append(admin1);
        }
        if (country != null && !country.isBlank()) {
            sb.append(", ").append(country);
        }
        return sb.toString();
    }
}
