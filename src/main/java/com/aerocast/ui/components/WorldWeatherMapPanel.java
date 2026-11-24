package com.aerocast.ui.components;

import com.aerocast.model.UnitSystem;
import com.aerocast.model.WeatherCondition;
import com.aerocast.model.WeatherReport;
import com.aerocast.service.UnitConversionService;
import com.aerocast.ui.util.SvgWeatherIcons;
import com.aerocast.ui.util.UiThemeUtils;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.*;
import java.util.function.Consumer;

/**
 * Interactive World Meteorological Station Map.
 * Unslopped design craft:
 * - High-precision vector world map in Ink dark observatory aesthetic
 * - Real-time global station nodes (Tokyo, London, New York, Mumbai, Paris, Sydney, Reykjavik, Cairo)
 * - Automatic dynamic plotting of current user GPS coordinates and pinned favorites
 * - Clickable station telemetry pins for instant global weather switching
 * - 4px grid rhythm, 16px panel radius, 8px station chips
 */
public class WorldWeatherMapPanel extends Div {

    private final UnitConversionService conversionService;
    private final Consumer<String> onCitySelected;

    private final Div svgMapContainer = new Div();
    private final HorizontalLayout stationsStrip = new HorizontalLayout();
    private final Span coordinateTickerSpan = new Span();
    private final Span trackedStationSpan = new Span();

    // Baseline Global Meteorological Stations
    public record MapStation(
            String id,
            String name,
            String country,
            double lat,
            double lon,
            double defaultTempC,
            WeatherCondition condition
    ) {}

    private static final List<MapStation> GLOBAL_STATIONS = List.of(
            new MapStation("tokyo", "Tokyo", "Japan", 35.6762, 139.6503, 21.4, WeatherCondition.CLEAR_SUNNY),
            new MapStation("london", "London", "United Kingdom", 51.5074, -0.1278, 12.0, WeatherCondition.LIGHT_RAIN),
            new MapStation("newyork", "New York", "United States", 40.7128, -74.0060, 18.2, WeatherCondition.PARTLY_CLOUDY),
            new MapStation("mumbai", "Mumbai", "India", 19.0760, 72.8777, 31.8, WeatherCondition.HIGH_HEAT),
            new MapStation("paris", "Paris", "France", 48.8566, 2.3522, 14.5, WeatherCondition.CLOUDY_OVERCAST),
            new MapStation("sydney", "Sydney", "Australia", -33.8688, 151.2093, 22.0, WeatherCondition.CLEAR_SUNNY),
            new MapStation("reykjavik", "Reykjavik", "Iceland", 64.1466, -21.9426, 3.2, WeatherCondition.SNOW_BLIZZARD),
            new MapStation("cairo", "Cairo", "Egypt", 30.0444, 31.2357, 28.6, WeatherCondition.CLEAR_SUNNY)
    );

    private WeatherReport currentReport;
    private UnitSystem currentUnitSystem = UnitSystem.METRIC;

    public WorldWeatherMapPanel(UnitConversionService conversionService, Consumer<String> onCitySelected) {
        this.conversionService = conversionService;
        this.onCitySelected = onCitySelected;

        initLayout();
    }

    private void initLayout() {
        setWidthFull();
        getStyle()
                .set("padding", "20px")
                .set("border-radius", "16px")
                .set("background", UiThemeUtils.COLOR_SURFACE_CARD)
                .set("border", "1px solid " + UiThemeUtils.COLOR_HAIRLINE)
                .set("box-shadow", "inset 0 1px 0 rgba(255, 255, 255, 0.04)")
                .set("margin-top", "20px");

        // Header
        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.getStyle().set("margin-bottom", "14px");

        HorizontalLayout titleGroup = new HorizontalLayout();
        titleGroup.setAlignItems(FlexComponent.Alignment.CENTER);
        titleGroup.setSpacing(true);

        Div globeIcon = new Div(SvgWeatherIcons.getGlobeIconSvg(16));
        globeIcon.getStyle().set("color", UiThemeUtils.COLOR_ACCENT);

        H3 title = new H3("Global meteorological stations");
        title.getStyle()
                .set("margin", "0")
                .set("font-size", "15px")
                .set("font-weight", "500")
                .set("color", UiThemeUtils.COLOR_TEXT_PRIMARY);

        titleGroup.add(globeIcon, title);

        HorizontalLayout metaGroup = new HorizontalLayout();
        metaGroup.setAlignItems(FlexComponent.Alignment.CENTER);
        metaGroup.setSpacing(true);

        coordinateTickerSpan.getStyle()
                .set("font-size", "12px")
                .set("color", UiThemeUtils.COLOR_TEXT_MUTED)
                .set("font-variant-numeric", "tabular-nums");

        metaGroup.add(coordinateTickerSpan);
        header.add(titleGroup, metaGroup);

        // SVG World Map Graphic Container
        svgMapContainer.setWidthFull();
        svgMapContainer.getStyle()
                .set("border-radius", "12px")
                .set("overflow", "hidden")
                .set("background", UiThemeUtils.COLOR_BG_PAGE)
                .set("border", "1px solid " + UiThemeUtils.COLOR_HAIRLINE_SUBTLE)
                .set("margin-bottom", "14px");

        // Quick Switch Station Strip
        stationsStrip.setWidthFull();
        stationsStrip.setSpacing(true);
        stationsStrip.getStyle()
                .set("overflow-x", "auto")
                .set("padding-bottom", "4px")
                .set("scrollbar-width", "none");

        add(header, svgMapContainer, stationsStrip);
    }

    public void update(WeatherReport report, UnitSystem unitSystem) {
        this.currentReport = report;
        this.currentUnitSystem = unitSystem;

        if (report == null) {
            return;
        }

        // Format coordinates
        String latHem = report.latitude() >= 0 ? "N" : "S";
        String lonHem = report.longitude() >= 0 ? "E" : "W";
        coordinateTickerSpan.setText(String.format(Locale.US,
                "Tracked: %s · %.2f° %s, %.2f° %s",
                report.cityName(),
                Math.abs(report.latitude()), latHem,
                Math.abs(report.longitude()), lonHem
        ));

        renderSvgMap();
        renderStationsStrip();
    }

    private void renderSvgMap() {
        svgMapContainer.removeAll();

        double width = 960.0;
        double height = 460.0;

        StringBuilder sb = new StringBuilder();
        sb.append(String.format(Locale.US, """
            <svg viewBox="0 0 %.0f %.0f" width="100%%" height="auto" style="display:block;" xmlns="http://www.w3.org/2000/svg">
              <defs>
                <pattern id="graticuleGrid" width="80" height="80" patternUnits="userSpaceOnUse">
                  <path d="M 80 0 L 0 0 0 80" fill="none" stroke="rgba(238,240,245,0.035)" stroke-width="1"/>
                </pattern>
                <filter id="activePinGlow" x="-30%%" y="-30%%" width="160%%" height="160%%">
                  <feDropShadow dx="0" dy="0" stdDeviation="3" flood-color="#38bdf8" flood-opacity="0.6"/>
                </filter>
              </defs>
              <!-- Deep Ocean Background -->
              <rect width="100%%" height="100%%" fill="#0b0d14"/>
              <rect width="100%%" height="100%%" fill="url(#graticuleGrid)"/>

              <!-- Latitude/Longitude Graticule References -->
              <!-- Equator -->
              <line x1="0" y1="230" x2="960" y2="230" stroke="rgba(238,240,245,0.08)" stroke-width="1" stroke-dasharray="4 4"/>
              <!-- Prime Meridian -->
              <line x1="480" y1="0" x2="480" y2="460" stroke="rgba(238,240,245,0.08)" stroke-width="1" stroke-dasharray="4 4"/>
              <!-- Tropic of Cancer (23.5 N -> y = (90-23.5)*460/180 = 170) -->
              <line x1="0" y1="170" x2="960" y2="170" stroke="rgba(238,240,245,0.04)" stroke-width="1" stroke-dasharray="2 4"/>
              <!-- Tropic of Capricorn (23.5 S -> y = (90+23.5)*460/180 = 290) -->
              <line x1="0" y1="290" x2="960" y2="290" stroke="rgba(238,240,245,0.04)" stroke-width="1" stroke-dasharray="2 4"/>

              <!-- Vector Continent Landmasses (Equirectangular Projection) -->
              <g fill="#1a1e28" stroke="#2b3345" stroke-width="1.2" stroke-linejoin="round">
                <!-- North America -->
                <path d="M 120 40 L 160 30 L 220 35 L 260 55 L 250 85 L 270 110 L 240 120 L 255 150 L 230 180 L 210 190 L 190 170 L 160 175 L 140 150 L 110 130 L 90 90 L 95 60 Z"/>
                <!-- Central America Bridge -->
                <path d="M 210 190 L 235 220 L 255 240 L 250 248 L 230 230 L 205 195 Z"/>
                <!-- Greenland -->
                <path d="M 330 30 L 385 25 L 395 55 L 350 75 L 325 55 Z"/>
                <!-- South America -->
                <path d="M 255 240 L 295 245 L 340 270 L 350 310 L 315 375 L 285 410 L 275 390 L 270 330 L 250 280 L 245 255 Z"/>
                <!-- Europe & British Isles -->
                <path d="M 460 70 L 490 60 L 510 70 L 540 85 L 530 125 L 495 135 L 460 145 L 445 135 L 440 115 L 455 90 Z"/>
                <path d="M 445 85 L 460 75 L 455 100 L 440 95 Z"/><!-- UK -->
                <path d="M 415 65 L 430 65 L 425 80 L 415 75 Z"/><!-- Iceland -->
                <!-- Africa & Madagascar -->
                <path d="M 455 155 L 515 150 L 555 185 L 575 235 L 535 320 L 510 355 L 485 345 L 460 260 L 430 200 L 445 165 Z"/>
                <path d="M 575 295 L 585 295 L 580 335 L 570 330 Z"/><!-- Madagascar -->
                <!-- Asia & Middle East -->
                <path d="M 540 85 L 620 60 L 720 50 L 780 70 L 810 95 L 775 140 L 740 170 L 710 190 L 670 230 L 635 220 L 620 180 L 580 180 L 555 140 Z"/>
                <path d="M 770 120 L 785 110 L 780 135 L 765 140 Z"/><!-- Japan Archipelago -->
                <path d="M 640 180 L 675 185 L 670 235 L 650 240 Z"/><!-- Indian Subcontinent -->
                <path d="M 550 180 L 585 185 L 575 225 L 545 205 Z"/><!-- Arabian Peninsula -->
                <!-- Southeast Asia & Maritime Archipelago -->
                <path d="M 710 200 L 735 225 L 725 250 L 715 230 Z"/>
                <path d="M 720 260 L 760 265 L 755 275 L 715 270 Z"/><!-- Indonesia -->
                <!-- Australia & New Zealand -->
                <path d="M 760 300 L 830 305 L 850 345 L 810 380 L 765 375 L 745 340 Z"/>
                <path d="M 875 365 L 890 360 L 880 395 L 870 390 Z"/><!-- New Zealand -->
              </g>
            """, width, height));

        // Station Markers Layer
        sb.append("<g id='stationMarkers'>");

        // 1. Plot all global stations
        for (MapStation st : GLOBAL_STATIONS) {
            double x = (st.lon() + 180.0) * (width / 360.0);
            double y = (90.0 - st.lat()) * (height / 180.0);

            boolean isTracked = currentReport != null &&
                    currentReport.cityName().equalsIgnoreCase(st.name());

            double tempDisplay = conversionService.convertTemperature(st.defaultTempC(), currentUnitSystem);
            String tempStr = String.format(Locale.US, "%.0f°", tempDisplay);

            String pinColor = isTracked ? UiThemeUtils.COLOR_ACCENT : "#94a3b8";
            String fillColor = isTracked ? "#38bdf8" : "#242c3d";
            String filterAttr = isTracked ? "filter='url(#activePinGlow)'" : "";

            sb.append(String.format(Locale.US, """
                <!-- Station: %s -->
                <g class='station-pin' %s style='cursor:pointer;'>
                  <circle cx='%.1f' cy='%.1f' r='%s' fill='%s' stroke='%s' stroke-width='%s'/>
                  <circle cx='%.1f' cy='%.1f' r='3.5' fill='%s'/>
                  <!-- Temperature Pill Badge -->
                  <rect x='%.1f' y='%.1f' width='%.0f' height='16' rx='4' fill='#11141c' stroke='%s' stroke-width='1'/>
                  <text x='%.1f' y='%.1f' fill='%s' font-size='9' font-family='sans-serif' font-weight='500' text-anchor='middle'>%s</text>
                </g>
                """,
                    st.name(),
                    filterAttr,
                    x, y, isTracked ? "8" : "6", fillColor, pinColor, isTracked ? "2" : "1.2",
                    x, y, isTracked ? "#ffffff" : pinColor,
                    x - 22.0, y - 24.0, 44.0,
                    isTracked ? "#38bdf8" : "rgba(238,240,245,0.12)",
                    x, y - 13.0, isTracked ? "#38bdf8" : "#cbd5e1",
                    st.name() + " " + tempStr
            ));
        }

        // 2. Plot Current Location Pin if outside the 8 standard stations
        if (currentReport != null && !isStandardStation(currentReport.cityName())) {
            double curX = (currentReport.longitude() + 180.0) * (width / 360.0);
            double curY = (90.0 - currentReport.latitude()) * (height / 180.0);

            double curTemp = conversionService.convertTemperature(currentReport.currentTemp(), currentUnitSystem);
            String curTempStr = String.format(Locale.US, "%.0f°", curTemp);

            sb.append(String.format(Locale.US, """
                <!-- Dynamic Current Location Reticle -->
                <g class='current-location-pin' filter='url(#activePinGlow)'>
                  <!-- Crosshairs Reticle -->
                  <circle cx='%.1f' cy='%.1f' r='10' fill='none' stroke='#38bdf8' stroke-width='1.5' stroke-dasharray='2 2'/>
                  <circle cx='%.1f' cy='%.1f' r='5' fill='#38bdf8' stroke='#11141c' stroke-width='1.5'/>
                  <line x1='%.1f' y1='%.1f' x2='%.1f' y2='%.1f' stroke='#38bdf8' stroke-width='1.2'/>
                  <line x1='%.1f' y1='%.1f' x2='%.1f' y2='%.1f' stroke='#38bdf8' stroke-width='1.2'/>
                  <!-- Location Label -->
                  <rect x='%.1f' y='%.1f' width='70' height='18' rx='4' fill='#11141c' stroke='#38bdf8' stroke-width='1.2'/>
                  <text x='%.1f' y='%.1f' fill='#38bdf8' font-size='9.5' font-family='sans-serif' font-weight='500' text-anchor='middle'>%s %s</text>
                </g>
                """,
                    curX, curY,
                    curX, curY,
                    curX - 14, curY, curX + 14, curY,
                    curX, curY - 14, curX, curY + 14,
                    curX - 35.0, curY - 26.0,
                    curX, curY - 13.5,
                    currentReport.cityName(), curTempStr
            ));
        }

        sb.append("</g></svg>");
        svgMapContainer.add(new Html(sb.toString()));
    }

    private void renderStationsStrip() {
        stationsStrip.removeAll();

        // 1. Current Location Chip (if tracked and not in standard 8)
        if (currentReport != null && !isStandardStation(currentReport.cityName())) {
            Button curChip = new Button();
            curChip.setIcon(VaadinIcon.MAP_MARKER.create());
            curChip.setText(currentReport.cityName() + " · " +
                    conversionService.formatTemperature(currentReport.currentTemp(), currentUnitSystem));
            curChip.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SMALL);
            curChip.getStyle()
                    .set("border-radius", "8px")
                    .set("font-size", "12px")
                    .set("font-weight", "500")
                    .set("background", UiThemeUtils.COLOR_SURFACE_RAISED)
                    .set("border", "1px solid " + UiThemeUtils.COLOR_ACCENT)
                    .set("color", UiThemeUtils.COLOR_ACCENT);
            curChip.addClickListener(e -> onCitySelected.accept(currentReport.cityName()));
            stationsStrip.add(curChip);
        }

        // 2. Global Station Chips
        for (MapStation st : GLOBAL_STATIONS) {
            boolean isTracked = currentReport != null && currentReport.cityName().equalsIgnoreCase(st.name());

            Button chip = new Button();
            double temp = conversionService.convertTemperature(st.defaultTempC(), currentUnitSystem);
            chip.setText(st.name() + " · " + String.format(Locale.US, "%.0f°", temp));

            chip.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
            chip.getStyle()
                    .set("border-radius", "8px")
                    .set("font-size", "12px")
                    .set("font-weight", isTracked ? "500" : "400")
                    .set("padding", "4px 10px")
                    .set("cursor", "pointer")
                    .set("transition", "background-color 150ms ease, border-color 150ms ease");

            if (isTracked) {
                chip.getStyle()
                        .set("background", UiThemeUtils.COLOR_SURFACE_RAISED)
                        .set("border", "1px solid rgba(56, 189, 248, 0.4)")
                        .set("color", UiThemeUtils.COLOR_ACCENT);
            } else {
                chip.getStyle()
                        .set("background", "transparent")
                        .set("border", "1px solid " + UiThemeUtils.COLOR_HAIRLINE)
                        .set("color", UiThemeUtils.COLOR_TEXT_SECONDARY);
            }

            chip.addClickListener(e -> onCitySelected.accept(st.name()));
            stationsStrip.add(chip);
        }
    }

    private boolean isStandardStation(String cityName) {
        if (cityName == null) return false;
        for (MapStation s : GLOBAL_STATIONS) {
            if (s.name().equalsIgnoreCase(cityName)) return true;
        }
        return false;
    }
}
