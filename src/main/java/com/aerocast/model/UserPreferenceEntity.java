package com.aerocast.model;

import jakarta.persistence.*;

/**
 * Key-value configuration entity stored in H2 database (e.g. Unit preference, API keys).
 */
@Entity
@Table(name = "user_preferences")
public class UserPreferenceEntity {

    @Id
    @Column(name = "pref_key", length = 100)
    private String prefKey;

    @Column(name = "pref_value", length = 500)
    private String prefValue;

    public UserPreferenceEntity() {
    }

    public UserPreferenceEntity(String prefKey, String prefValue) {
        this.prefKey = prefKey;
        this.prefValue = prefValue;
    }

    public String getPrefKey() {
        return prefKey;
    }

    public void setPrefKey(String prefKey) {
        this.prefKey = prefKey;
    }

    public String getPrefValue() {
        return prefValue;
    }

    public void setPrefValue(String prefValue) {
        this.prefValue = prefValue;
    }
}
