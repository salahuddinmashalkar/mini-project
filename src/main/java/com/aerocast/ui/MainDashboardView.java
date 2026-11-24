package com.aerocast.ui;

import com.aerocast.model.*;
import com.aerocast.service.*;
import com.aerocast.ui.components.*;
import com.aerocast.ui.util.SvgWeatherIcons;
import com.aerocast.ui.util.UiThemeUtils;
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.flow.data.provider.DataProvider;
import jakarta.annotation.PostConstruct;
import java.util.List;

/**
 * AeroCast primary dashboard view.
 * Refactored using the unslop design craft guidelines:
 * - Ink dark neutral ramp (#0b0d14 page, #11141c cards, #1a1e28 raised surfaces)
 * - Restrained hairlines at 8% opacity, zero glowing drop-shadows or hue gradients
 * - Unified SVG line icons (zero random emojis)
 * - Concise, understated copy in sentence case
 * - Clean typographic weights (400 and 500) with tabular figures
 * - Strict 4px rhythm and nested component radii (16px cards, 8px controls)
 */
@Route("")
@PageTitle("AeroCast · Weather intelligence")
public class MainDashboardView extends Div {

    private final OpenMeteoWeatherService liveApiService;
    private final SampleWeatherService sampleService;
    private final UnitConversionService conversionService;
    private final AdvisoryEngineService advisoryEngineService;
    private final UserPreferenceService preferenceService;
    private final GroqWeatherAiService groqWeatherAiService;
    private final WeatherCacheService cacheService;
    private final LocationService locationService;

    // State
    private boolean isLiveApiActive = true;
    private UnitSystem currentUnitSystem = UnitSystem.METRIC;
    private String currentCity = "Tokyo";
    private String currentCountry = "Japan";
    private WeatherReport currentReport;

    // UI Sub-components
    private HeroWeatherCard heroCard;
    private FavoritesBar favoritesBar;
    private HourlyForecastPanel hourlyPanel;
    private DailyForecastPanel dailyPanel;
    private TelemetryGridPanel telemetryGrid;
    private SunMoonTrackerCard sunMoonCard;
    private LifestyleAdvisoryCard advisoryCard;
    private WorldWeatherMapPanel worldMapPanel;

    // Header Controls
    private final ComboBox<GeoLocation> citySearchBox = new ComboBox<>();
    private final Button searchBtn = new Button();
    private final Button currentLocationBtn = new Button();
    private final Button modeToggleBtn = new Button();
    private final Button unitToggleBtn = new Button();
    private final Button refreshBtn = new Button();
    private final Button settingsBtn = new Button();

    public MainDashboardView(OpenMeteoWeatherService liveApiService,
                             SampleWeatherService sampleService,
                             UnitConversionService conversionService,
                             AdvisoryEngineService advisoryEngineService,
                             UserPreferenceService preferenceService,
                             GroqWeatherAiService groqWeatherAiService,
                             WeatherCacheService cacheService,
                             LocationService locationService) {
        this.liveApiService = liveApiService;
        this.sampleService = sampleService;
        this.conversionService = conversionService;
        this.advisoryEngineService = advisoryEngineService;
        this.preferenceService = preferenceService;
        this.groqWeatherAiService = groqWeatherAiService;
        this.cacheService = cacheService;
        this.locationService = locationService;

        UI.getCurrent().getElement().getThemeList().add(Lumo.DARK);

        initView();
    }

    @PostConstruct
    public void setup() {
        this.currentUnitSystem = preferenceService.getUnitSystem();
        refreshDashboard(currentCity, currentCountry);
    }

    private void initView() {
        setSizeFull();
        getStyle()
                .set("background", UiThemeUtils.COLOR_BG_PAGE)
                .set("color", UiThemeUtils.COLOR_TEXT_PRIMARY)
                .set("min-height", "100vh")
                .set("overflow-y", "auto")
                .set("font-family", "system-ui, -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif");

        VerticalLayout mainContainer = new VerticalLayout();
        mainContainer.setMaxWidth("1440px");
        mainContainer.getStyle()
                .set("margin", "0 auto")
                .set("padding", "24px 32px 48px 32px");

        // 1. Navigation Header
        HorizontalLayout header = buildHeader();

        // 2. Persistent Favorites Bar
        favoritesBar = new FavoritesBar(
                this::handleCitySelected,
                this::handleFavoriteRemoved
        );

        // 3. Main Dashboard Grid
        heroCard = new HeroWeatherCard(conversionService, this::handleFavoriteToggled);
        hourlyPanel = new HourlyForecastPanel(conversionService);
        dailyPanel = new DailyForecastPanel(conversionService);
        telemetryGrid = new TelemetryGridPanel(conversionService);
        sunMoonCard = new SunMoonTrackerCard();
        advisoryCard = new LifestyleAdvisoryCard();
        worldMapPanel = new WorldWeatherMapPanel(conversionService, this::handleCitySelected);

        // Layout Columns: 2-column responsive layout
        HorizontalLayout bodyLayout = new HorizontalLayout();
        bodyLayout.setWidthFull();
        bodyLayout.setSpacing(true);
        bodyLayout.getStyle().set("margin-top", "16px");

        // Left Column (Hero, Hourly, Telemetry, Sun/Moon)
        VerticalLayout leftCol = new VerticalLayout();
        leftCol.setPadding(false);
        leftCol.setSpacing(true);
        leftCol.getStyle().set("flex", "1 1 65%");

        leftCol.add(heroCard, hourlyPanel, telemetryGrid, sunMoonCard);

        // Right Column (Daily 7-day, Advisories)
        VerticalLayout rightCol = new VerticalLayout();
        rightCol.setPadding(false);
        rightCol.setSpacing(true);
        rightCol.getStyle().set("flex", "1 1 35%");

        rightCol.add(dailyPanel, advisoryCard);

        bodyLayout.add(leftCol, rightCol);

        bodyLayout.getStyle()
                .set("display", "flex")
                .set("flex-wrap", "wrap")
                .set("gap", "20px");

        // 4. Footer
        Div footer = buildFooter();

        mainContainer.add(header, favoritesBar, bodyLayout, worldMapPanel, footer);
        add(mainContainer);
    }

    private HorizontalLayout buildHeader() {
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.getStyle()
                .set("padding-bottom", "16px")
                .set("border-bottom", "1px solid " + UiThemeUtils.COLOR_HAIRLINE)
                .set("margin-bottom", "16px");

        // Brand
        HorizontalLayout brand = new HorizontalLayout();
        brand.setAlignItems(FlexComponent.Alignment.CENTER);
        brand.setSpacing(true);

        Div stationIcon = new Div(SvgWeatherIcons.getStationIconSvg(18));
        stationIcon.getStyle()
                .set("display", "inline-flex")
                .set("align-items", "center");

        H2 brandTitle = new H2("AeroCast");
        brandTitle.getStyle()
                .set("margin", "0")
                .set("font-size", "18px")
                .set("font-weight", "500")
                .set("letter-spacing", "-0.02em")
                .set("color", UiThemeUtils.COLOR_TEXT_PRIMARY);

        Span signatureTag = new Span("Station telemetry");
        signatureTag.getStyle()
                .set("font-size", "11px")
                .set("font-weight", "400")
                .set("color", UiThemeUtils.COLOR_TEXT_MUTED)
                .set("margin-left", "6px");

        brand.add(stationIcon, brandTitle, signatureTag);

        // Universal Search Bar with Full City & Village Geocoding
        citySearchBox.setPlaceholder("Search any city, town, or village...");
        citySearchBox.setWidth("330px");
        citySearchBox.setClearButtonVisible(true);
        citySearchBox.setAllowCustomValue(true);
        citySearchBox.setItemLabelGenerator(GeoLocation::getDisplayName);
        citySearchBox.setDataProvider(
                (filterText, offset, limit) -> {
                    List<GeoLocation> results = locationService.searchLocations(filterText);
                    return results.stream().skip(offset).limit(limit);
                },
                filterText -> locationService.searchLocations(filterText).size()
        );
        citySearchBox.addValueChangeListener(e -> {
            if (e.isFromClient() && e.getValue() != null) {
                handleLocationSelected(e.getValue());
            }
        });
        citySearchBox.addCustomValueSetListener(e -> {
            String custom = e.getDetail();
            if (custom != null && !custom.isBlank()) {
                handleTextSearch(custom.trim());
            }
        });

        // Search Action Button
        searchBtn.setIcon(VaadinIcon.SEARCH.create());
        searchBtn.setTooltipText("Search location (Enter)");
        searchBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        searchBtn.getStyle()
                .set("color", UiThemeUtils.COLOR_TEXT_PRIMARY)
                .set("background", UiThemeUtils.COLOR_SURFACE_RAISED)
                .set("border", "1px solid " + UiThemeUtils.COLOR_HAIRLINE)
                .set("border-radius", "8px")
                .set("cursor", "pointer")
                .set("padding", "0 10px");
        searchBtn.addClickListener(e -> {
            citySearchBox.getElement().executeJs("return this.filter || (this.inputElement ? this.inputElement.value : '')")
                    .then(String.class, rawText -> {
                        if (rawText != null && !rawText.isBlank()) {
                            handleTextSearch(rawText.trim());
                        } else if (citySearchBox.getValue() != null) {
                            handleLocationSelected(citySearchBox.getValue());
                        } else {
                            Notification.show("Please enter a city, town, or village name", 2500, Notification.Position.BOTTOM_END);
                        }
                    });
        });

        HorizontalLayout searchGroup = new HorizontalLayout(citySearchBox, searchBtn);
        searchGroup.setAlignItems(FlexComponent.Alignment.CENTER);
        searchGroup.setSpacing(true);

        // Current Location Button
        Div locIcon = new Div(SvgWeatherIcons.getCurrentLocationIconSvg(14));
        locIcon.getStyle()
                .set("display", "inline-flex")
                .set("align-items", "center")
                .set("color", UiThemeUtils.COLOR_ACCENT);

        currentLocationBtn.setIcon(locIcon);
        currentLocationBtn.setText("My location");
        currentLocationBtn.setTooltipText("Detect current coordinates and local weather");
        currentLocationBtn.getStyle()
                .set("background", UiThemeUtils.COLOR_SURFACE_RAISED)
                .set("color", UiThemeUtils.COLOR_TEXT_PRIMARY)
                .set("border", "1px solid " + UiThemeUtils.COLOR_HAIRLINE)
                .set("border-radius", "8px")
                .set("font-size", "12px")
                .set("font-weight", "500")
                .set("cursor", "pointer")
                .set("padding", "0 12px");
        currentLocationBtn.addClickListener(e -> requestClientLocation());

        // Action Toolbar
        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.setAlignItems(FlexComponent.Alignment.CENTER);
        toolbar.setSpacing(true);

        toolbar.add(searchGroup, currentLocationBtn, modeToggleBtn, unitToggleBtn, refreshBtn, settingsBtn);

        // Mode Toggle
        updateModeToggleButton();
        modeToggleBtn.addClickListener(e -> {
            isLiveApiActive = !isLiveApiActive;
            updateModeToggleButton();
            refreshDashboard(currentCity, currentCountry);
            Notification notif = Notification.show(
                    isLiveApiActive ? "Live observation connected" : "Sample observation mode active",
                    2500, Notification.Position.BOTTOM_END);
            notif.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
        });

        // Unit Toggle
        updateUnitToggleButton();
        unitToggleBtn.addClickListener(e -> {
            currentUnitSystem = (currentUnitSystem == UnitSystem.METRIC) ? UnitSystem.IMPERIAL : UnitSystem.METRIC;
            preferenceService.setUnitSystem(currentUnitSystem);
            updateUnitToggleButton();
            refreshDashboard(currentCity, currentCountry);
        });

        // Refresh Button
        refreshBtn.setIcon(VaadinIcon.REFRESH.create());
        refreshBtn.setTooltipText("Refresh telemetry");
        refreshBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        refreshBtn.getStyle().set("color", UiThemeUtils.COLOR_TEXT_MUTED);
        refreshBtn.addClickListener(e -> {
            cacheService.evict(currentCity);
            refreshDashboard(currentCity, currentCountry);
            Notification.show("Telemetry synchronized", 2000, Notification.Position.BOTTOM_END);
        });

        // Settings Button
        settingsBtn.setIcon(VaadinIcon.COG.create());
        settingsBtn.setTooltipText("Settings");
        settingsBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        settingsBtn.getStyle().set("color", UiThemeUtils.COLOR_TEXT_MUTED);
        settingsBtn.addClickListener(e -> {
            SettingsDialog dialog = new SettingsDialog(preferenceService, () -> {
                this.currentUnitSystem = preferenceService.getUnitSystem();
                updateUnitToggleButton();
                refreshDashboard(currentCity, currentCountry);
            });
            dialog.open();
        });

        header.add(brand, toolbar);
        return header;
    }

    private void updateModeToggleButton() {
        if (isLiveApiActive) {
            modeToggleBtn.setText("Live broadcast");
            modeToggleBtn.getStyle()
                    .set("background", "rgba(76, 195, 138, 0.12)")
                    .set("color", UiThemeUtils.COLOR_POSITIVE)
                    .set("border", "1px solid rgba(76, 195, 138, 0.3)")
                    .set("border-radius", "8px")
                    .set("font-size", "12px")
                    .set("font-weight", "500");
        } else {
            modeToggleBtn.setText("Sample observation");
            modeToggleBtn.getStyle()
                    .set("background", UiThemeUtils.COLOR_SURFACE_RAISED)
                    .set("color", UiThemeUtils.COLOR_TEXT_SECONDARY)
                    .set("border", "1px solid " + UiThemeUtils.COLOR_HAIRLINE)
                    .set("border-radius", "8px")
                    .set("font-size", "12px")
                    .set("font-weight", "500");
        }
    }

    private void updateUnitToggleButton() {
        unitToggleBtn.setText(currentUnitSystem == UnitSystem.METRIC ? "Metric °C" : "Imperial °F");
        unitToggleBtn.getStyle()
                .set("background", UiThemeUtils.COLOR_SURFACE_RAISED)
                .set("color", UiThemeUtils.COLOR_TEXT_PRIMARY)
                .set("border", "1px solid " + UiThemeUtils.COLOR_HAIRLINE)
                .set("border-radius", "8px")
                .set("font-size", "12px")
                .set("font-weight", "500");
    }

    private WeatherDataProvider getActiveProvider() {
        return isLiveApiActive ? liveApiService : sampleService;
    }

    private void refreshDashboard(String cityName, String country) {
        this.currentCity = cityName;
        this.currentCountry = country;

        try {
            WeatherDataProvider provider = getActiveProvider();
            this.currentReport = provider.getWeather(cityName, country);

            String aiBriefing = groqWeatherAiService.generateBriefing(currentReport);

            boolean isFavorited = preferenceService.isCityFavorited(cityName);

            // Update UI Panels
            heroCard.update(currentReport, currentUnitSystem, isFavorited);
            favoritesBar.updateFavorites(preferenceService.getFavoriteCities(), cityName);
            hourlyPanel.update(currentReport.hourlyForecasts(), currentUnitSystem);
            dailyPanel.update(currentReport.dailyForecasts(), currentUnitSystem);
            telemetryGrid.update(currentReport, currentUnitSystem);
            sunMoonCard.update(currentReport.astronomy());
            advisoryCard.update(currentReport.advisories(), aiBriefing);
            worldMapPanel.update(currentReport, currentUnitSystem);

        } catch (Exception e) {
            Notification notif = Notification.show("Unable to load telemetry: " + e.getMessage(), 4000, Notification.Position.TOP_CENTER);
            notif.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void requestClientLocation() {
        Notification.show("Detecting current coordinates...", 2500, Notification.Position.BOTTOM_END);
        UI.getCurrent().getPage().executeJs(
                "if ('geolocation' in navigator) {" +
                "  navigator.geolocation.getCurrentPosition(" +
                "    function(pos) { $0.$server.onBrowserLocationReceived(pos.coords.latitude, pos.coords.longitude); }," +
                "    function(err) { $0.$server.onBrowserLocationFailed(err.code, err.message || 'Permission denied'); }," +
                "    { timeout: 7000, maximumAge: 60000, enableHighAccuracy: false }" +
                "  );" +
                "} else {" +
                "  $0.$server.onBrowserLocationFailed(-1, 'Browser geolocation not supported');" +
                "}",
                getElement()
        );
    }

    @ClientCallable
    public void onBrowserLocationReceived(double latitude, double longitude) {
        handleCoordinatesSelected(latitude, longitude, null, null);
    }

    @ClientCallable
    public void onBrowserLocationFailed(int errorCode, String errorMessage) {
        GeoLocation ipLoc = locationService.getIpLocation();
        if (ipLoc != null) {
            handleCoordinatesSelected(ipLoc.latitude(), ipLoc.longitude(), ipLoc.cityName(), ipLoc.country());
        } else {
            Notification notif = Notification.show("Location detection unavailable: please use search bar", 3000, Notification.Position.BOTTOM_END);
            notif.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
        }
    }

    public void handleCoordinatesSelected(double lat, double lon, String suggestedCity, String suggestedCountry) {
        try {
            WeatherDataProvider provider = getActiveProvider();
            this.currentReport = provider.getWeatherByCoordinates(lat, lon, suggestedCity, suggestedCountry);
            this.currentCity = currentReport.cityName();
            this.currentCountry = currentReport.country();

            String aiBriefing = groqWeatherAiService.generateBriefing(currentReport);
            boolean isFavorited = preferenceService.isCityFavorited(currentCity);

            heroCard.update(currentReport, currentUnitSystem, isFavorited);
            favoritesBar.updateFavorites(preferenceService.getFavoriteCities(), currentCity);
            hourlyPanel.update(currentReport.hourlyForecasts(), currentUnitSystem);
            dailyPanel.update(currentReport.dailyForecasts(), currentUnitSystem);
            telemetryGrid.update(currentReport, currentUnitSystem);
            sunMoonCard.update(currentReport.astronomy());
            advisoryCard.update(currentReport.advisories(), aiBriefing);
            worldMapPanel.update(currentReport, currentUnitSystem);

            Notification notif = Notification.show(
                    "Current location synced: " + currentCity + " (" + String.format(java.util.Locale.US, "%.2f°, %.2f°", lat, lon) + ")",
                    3000, Notification.Position.BOTTOM_END);
            notif.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception e) {
            Notification notif = Notification.show("Unable to load coordinate telemetry: " + e.getMessage(), 3500, Notification.Position.TOP_CENTER);
            notif.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    private void handleLocationSelected(GeoLocation loc) {
        if (loc == null) return;
        handleCoordinatesSelected(loc.latitude(), loc.longitude(), loc.cityName(), loc.country());
    }

    private void handleTextSearch(String query) {
        if (query == null || query.isBlank()) return;
        Notification.show("Searching for '" + query + "'...", 1500, Notification.Position.BOTTOM_END);
        List<GeoLocation> matches = locationService.searchLocations(query.trim());
        if (!matches.isEmpty()) {
            handleLocationSelected(matches.get(0));
        } else {
            Notification notif = Notification.show("No location found for '" + query + "'. Check spelling or try a nearby city.", 3500, Notification.Position.BOTTOM_END);
            notif.addThemeVariants(NotificationVariant.LUMO_CONTRAST);
        }
    }

    private void handleCitySelected(String cityName) {
        citySearchBox.clear();
        if (cityName != null && !cityName.isBlank()) {
            List<GeoLocation> matches = locationService.searchLocations(cityName.trim());
            if (!matches.isEmpty()) {
                handleLocationSelected(matches.get(0));
                return;
            }
        }
        refreshDashboard(cityName, "");
    }

    private void handleFavoriteToggled(String cityName) {
        if (preferenceService.isCityFavorited(cityName)) {
            preferenceService.removeFavoriteCity(cityName);
            Notification.show("Removed " + cityName + " from pinned locations", 2000, Notification.Position.BOTTOM_END);
        } else {
            preferenceService.addFavoriteCity(cityName, currentReport != null ? currentReport.country() : "", 0, 0);
            Notification.show("Pinned " + cityName, 2000, Notification.Position.BOTTOM_END);
        }
        refreshDashboard(currentCity, currentCountry);
    }

    private void handleFavoriteRemoved(String cityName) {
        preferenceService.removeFavoriteCity(cityName);
        favoritesBar.updateFavorites(preferenceService.getFavoriteCities(), currentCity);
        if (currentReport != null) {
            heroCard.update(currentReport, currentUnitSystem, preferenceService.isCityFavorited(currentCity));
        }
        Notification.show("Unpinned " + cityName, 2000, Notification.Position.BOTTOM_END);
    }

    private Div buildFooter() {
        Div footer = new Div();
        footer.setWidthFull();
        footer.getStyle()
                .set("margin-top", "32px")
                .set("padding-top", "16px")
                .set("border-top", "1px solid " + UiThemeUtils.COLOR_HAIRLINE)
                .set("display", "flex")
                .set("justify-content", "space-between")
                .set("align-items", "center")
                .set("font-size", "12px")
                .set("color", UiThemeUtils.COLOR_TEXT_MUTED);

        Span leftText = new Span("AeroCast · Meteorological intelligence");
        Span rightText = new Span("Open-Meteo & observation telemetry");

        footer.add(leftText, rightText);
        return footer;
    }
}
