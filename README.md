# ⚡ AeroCast — Professional Weather Information Dashboard

A complete, production-grade, full-stack **Weather Information Dashboard** built **100% in Java** using **Spring Boot 3.3.x**, **Vaadin Flow 24.x**, **Spring Data JPA**, and an embedded **H2 Database**.

---

## 🌟 Key Architecture & Highlights

- **100% Pure Java Across All Tiers:** Zero Node.js, React, Angular, Vue, or JavaScript frontend frameworks. The entire reactive UI, event listeners, dynamic SVG renderers, and state management are implemented in pure Java.
- **Dual Data Engine Architecture:**
  - **Predefined Sample Data Engine (`SampleWeatherService`):** Rich, realistic meteorological datasets for **8 global cities** (*Tokyo, London, New York, Mumbai, Paris, Sydney, Reykjavik, and Cairo*) covering diverse weather states (Sunny, Heavy Rain, Snow Blizzard, Fog, Partly Cloudy, Extreme Heat).
  - **Live Weather API Engine (`OpenMeteoWeatherService`):** Native `java.net.http.HttpClient` queries to the free Open-Meteo Geocoding, Forecast, and Air Quality APIs with automatic failover.
- **Persistent H2 Embedded Storage:** Stores pinned/favorite cities, unit preferences, and user API credentials with Spring Data JPA and Hibernate.
- **Bespoke, Human-Crafted Modern Dark UI:** Glassmorphism styling (`backdrop-filter: blur(16px)`), dynamic weather-adaptive gradient hero card, and pure Java-rendered inline SVGs.
- **BYOK (Bring Your Own Key) for Groq AI Analysis:** Safe user-configured Groq API key via the Settings Dialog (⚙️) to unlock AI Chief Meteorologist briefings without hardcoded credentials.

---

## 🚀 The 7 Major Dashboard Features

1. **Dynamic Weather-Adaptive Hero Card:**
   - City, country, local time, big temperature, "Feels Like", condition description, high/low summary, and handcrafted pure-Java SVG weather icon.
   - Dynamic CSS gradient & accent border shifting automatically based on weather condition (Sunny amber, Rain slate-blue, Snow icy cyan, Storm electric purple, Heat red, Night cosmic indigo).
2. **City Search & Persistent Favorites Bar:**
   - Geocoding autocomplete search bar for worldwide cities.
   - Quick-switch interactive chips for pinned cities with one-click deletion and "Pin Current City" star toggle.
3. **24-Hour Interactive Forecast:**
   - Real-time SVG temperature curve visualization and precipitation probability area.
   - Horizontally scrollable row of 24 hourly cards showing time, weather condition SVG, temperature, and rain chance.
4. **7-Day Extended Forecast with Proportional Thermal Range Bars:**
   - Day of week, weather icon, rain chance, and proportional thermal range bars comparing each day's low/high relative to the full 7-day spectrum.
5. **Air Quality Index (AQI) & Atmospheric Telemetry Grid:**
   - **AQI Card:** US EPA color-coded badge (Good, Moderate, Unhealthy, Hazardous) with granular PM2.5, PM10, and Ozone ($O_3$) levels.
   - **UV Solar Index:** Numerical rating, risk badge, and safety precautions.
   - **Wind Telemetry:** Speed plus dynamically rotated SVG directional compass rose showing exact cardinal heading.
   - **Humidity & Dew Point:** Relative humidity percentage and psychrometric dew point calculation.
   - **Optical Visibility:** Distance in km or miles and atmospheric clarity.
   - **Barometric Pressure:** Pressure in hPa or inHg and atmospheric front trend.
6. **Sun & Moon Cycle Tracker:**
   - Handcrafted Java-rendered SVG semicircular solar arc plotting the sun's real-time position between Sunrise and Sunset with daylight duration.
   - Moon Phase graphic with illumination percentage and exact astronomical phase name.
7. **Global Unit Converter & Smart Lifestyle Advisories:**
   - Instant toggle between **Metric (`°C`, `km/h`, `hPa`, `km`)** and **Imperial (`°F`, `mph`, `inHg`, `mi`)**.
   - Rule-based `AdvisoryEngineService` computing tailored recommendations for Wardrobe, Hydration, Running/Fitness, and Commute road safety.
   - Optional AI Meteorological Synthesis powered by Groq LLM when configured by the user.

---

## 🛠️ Project Structure

```
c:\Users\Admin\Desktop\salahuddin-project\
├── pom.xml                                   # Complete Maven dependencies & Vaadin plugin
├── README.md                                 # Architecture & operational guide
├── frontend\themes\aerocast\styles.css        # Theme styles & dark-mode overrides
├── src\
│   ├── main\
│   │   ├── java\com\aerocast\
│   │   │   ├── AeroCastApplication.java      # Spring Boot & AppShellConfigurator entry point
│   │   │   ├── model\
│   │   │   │   ├── WeatherCondition.java     # Weather enum with WMO code mappings & color palettes
│   │   │   │   ├── UnitSystem.java           # Metric vs Imperial definitions & symbols
│   │   │   │   ├── GeoLocation.java          # Record for coordinates & administrative boundaries
│   │   │   │   ├── HourlyForecast.java       # Record for 24h hourly intervals
│   │   │   │   ├── DailyForecast.java        # Record for 7-day extended forecasts
│   │   │   │   ├── AirQualityMetrics.java    # Record for EPA AQI, PM2.5, PM10, Ozone
│   │   │   │   ├── AstronomyData.java        # Record for Solar arc, Sunrise/Sunset, Moon phase
│   │   │   │   ├── LifestyleAdvisory.java    # Record for lifestyle recommendation cards
│   │   │   │   ├── WeatherReport.java        # Root domain aggregate record
│   │   │   │   ├── FavoriteCityEntity.java   # JPA entity for H2 pinned cities
│   │   │   │   └── UserPreferenceEntity.java # JPA entity for key-value configuration
│   │   │   ├── repository\
│   │   │   │   ├── FavoriteCityRepository.java
│   │   │   │   └── UserPreferenceRepository.java
│   │   │   ├── service\
│   │   │   │   ├── WeatherDataProvider.java     # Strategy interface
│   │   │   │   ├── SampleWeatherService.java    # Predefined 8-city offline engine
│   │   │   │   ├── OpenMeteoWeatherService.java # Live Open-Meteo REST provider
│   │   │   │   ├── GroqWeatherAiService.java    # Pure Java HttpClient Groq AI integration
│   │   │   │   ├── UnitConversionService.java   # High-precision mathematical conversions
│   │   │   │   ├── AdvisoryEngineService.java   # Meteorological rules engine
│   │   │   │   └── UserPreferenceService.java   # JPA preference & favorites persistence
│   │   │   └── ui\
│   │   │       ├── MainDashboardView.java       # Vaadin Flow root UI view
│   │   │       ├── components\
│   │   │       │   ├── HeroWeatherCard.java     # Weather-adaptive hero card
│   │   │       │   ├── FavoritesBar.java        # Pinned city interactive chips
│   │   │       │   ├── HourlyForecastPanel.java # 24h timeline with SVG curve
│   │   │       │   ├── DailyForecastPanel.java  # 7-day list with thermal range bars
│   │   │       │   ├── TelemetryGridPanel.java  # 6-card atmospheric telemetry grid
│   │   │       │   ├── SunMoonTrackerCard.java  # Solar arc & lunar phase graphics
│   │   │       │   ├── LifestyleAdvisoryCard.java # Wardrobe, fitness & AI insight
│   │   │       │   └── SettingsDialog.java      # Dialog for Groq key & preferences
│   │   │       └── util\
│   │   │           ├── SvgWeatherIcons.java     # Pure Java SVG vector generator
│   │   │           └── UiThemeUtils.java        # Dynamic CSS gradients & styles
│   │   └── resources\
│   │       ├── application.properties           # H2, server port & Vaadin settings
│   │       ├── sample-weather-data.json         # Standalone 8-city JSON dataset
│   │       └── META-INF\resources\frontend\themes\aerocast\styles.css
│   └── test\java\com\aerocast\
│       ├── UnitConversionServiceTest.java       # Unit conversion verification
│       └── AdvisoryEngineServiceTest.java       # Rules engine verification
```

---

## ⚡ Quick Start & Execution Instructions

### Prerequisites
- **Java 21** (or Java 17+) JDK installed.
- **Apache Maven 3.8+** installed.

### 1. Build and Run
Open your terminal in the project directory:
```bash
mvn spring-boot:run
```

### 2. Access the Application
- Open your browser at: **[http://localhost:8080](http://localhost:8080)**
- To inspect the embedded database directly, navigate to the H2 Console: **[http://localhost:8080/h2-console](http://localhost:8080/h2-console)**
  - JDBC URL: `jdbc:h2:file:./data/aerocastdb`
  - User Name: `sa`
  - Password: *(blank)*

### 3. Adding Your Groq API Key
1. Click the **⚙️ Settings** icon in the top right corner of the dashboard.
2. Paste your personal **Groq API Key** (e.g., `gsk_...`).
3. Click **Save Settings**.
4. AeroCast will automatically activate the **AI Meteorologist Synthesis** banner powered by Groq!
