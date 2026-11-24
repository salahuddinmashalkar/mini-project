package com.aerocast.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * JPA Entity for persisting user favorite / pinned cities in H2 database.
 */
@Entity
@Table(name = "favorite_cities")
public class FavoriteCityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String cityName;

    @Column(nullable = false, length = 100)
    private String country;

    private double latitude;

    private double longitude;

    private int displayOrder;

    @Column(nullable = false)
    private LocalDateTime pinnedAt;

    public FavoriteCityEntity() {
    }

    public FavoriteCityEntity(String cityName, String country, double latitude, double longitude, int displayOrder) {
        this.cityName = cityName;
        this.country = country;
        this.latitude = latitude;
        this.longitude = longitude;
        this.displayOrder = displayOrder;
        this.pinnedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }

    public LocalDateTime getPinnedAt() {
        return pinnedAt;
    }

    public void setPinnedAt(LocalDateTime pinnedAt) {
        this.pinnedAt = pinnedAt;
    }
}
