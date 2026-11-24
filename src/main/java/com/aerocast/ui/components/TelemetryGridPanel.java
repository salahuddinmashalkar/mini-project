package com.aerocast.ui.components;

import com.aerocast.model.AirQualityMetrics;
import com.aerocast.model.UnitSystem;
import com.aerocast.model.WeatherReport;
import com.aerocast.service.UnitConversionService;
import com.aerocast.ui.util.SvgWeatherIcons;
import com.aerocast.ui.util.UiThemeUtils;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.Locale;

/**
 * Atmospheric telemetry grid.
 * Unslopped:
 * - Clean SVG line icons replacing emojis
 * - Sentence case headings with 400/500 weights
 * - Tabular figures (tabular-nums) for precise reading
 * - Strict 4px rhythm, 16px radius, quiet hairlines
 */
public class TelemetryGridPanel extends Div {

    private final UnitConversionService conversionService;

    // Card 1: AQI
    private final Span aqiValueSpan = new Span();
    private final Span aqiBadgeSpan = new Span();
    private final Span aqiDetailsSpan = new Span();
    private final Div aqiArcMeterDiv = new Div();

    // Card 2: UV Index
    private final Span uvValueSpan = new Span();
    private final Span uvBadgeSpan = new Span();
    private final Span uvAdviceSpan = new Span();
    private final Div uvArcMeterDiv = new Div();

    // Card 3: Wind Vector
    private final Div windCompassDiv = new Div();
    private final Span windSpeedSpan = new Span();
    private final Span windDirectionSpan = new Span();

    // Card 4: Humidity & Dew Point
    private final Span humidityValueSpan = new Span();
    private final Span dewPointSpan = new Span();

    // Card 5: Visibility
    private final Span visibilityValueSpan = new Span();
    private final Span visibilityRatingSpan = new Span();

    // Card 6: Pressure
    private final Span pressureValueSpan = new Span();
    private final Span pressureTrendSpan = new Span();

    public TelemetryGridPanel(UnitConversionService conversionService) {
        this.conversionService = conversionService;

        initLayout();
    }

    private void initLayout() {
        setWidthFull();
        getStyle()
                .set("display", "grid")
                .set("grid-template-columns", "repeat(auto-fit, minmax(280px, 1fr))")
                .set("gap", "16px");

        add(buildAqiCard());
        add(buildUvCard());
        add(buildWindCard());
        add(buildHumidityCard());
        add(buildVisibilityCard());
        add(buildPressureCard());
    }

    private Div buildBaseCard(String title, Html iconSvg) {
        Div card = new Div();
        card.getStyle()
                .set("padding", "20px")
                .set("border-radius", "16px")
                .set("background", UiThemeUtils.COLOR_SURFACE_CARD)
                .set("border", "1px solid " + UiThemeUtils.COLOR_HAIRLINE)
                .set("box-shadow", "inset 0 1px 0 rgba(255, 255, 255, 0.04)")
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("justify-content", "space-between");

        HorizontalLayout header = new HorizontalLayout();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setSpacing(true);
        header.getStyle().set("margin-bottom", "12px");

        Div iconBox = new Div(iconSvg);
        iconBox.getStyle()
                .set("display", "inline-flex")
                .set("align-items", "center")
                .set("width", "18px");

        H4 titleH = new H4(title);
        titleH.getStyle()
                .set("margin", "0")
                .set("font-size", "13px")
                .set("font-weight", "500")
                .set("color", UiThemeUtils.COLOR_TEXT_SECONDARY);

        header.add(iconBox, titleH);
        card.add(header);
        return card;
    }

    private Div buildAqiCard() {
        Div card = buildBaseCard("Air quality", SvgWeatherIcons.getAqiIconSvg(16));

        HorizontalLayout body = new HorizontalLayout();
        body.setWidthFull();
        body.setAlignItems(FlexComponent.Alignment.CENTER);
        body.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        VerticalLayout stats = new VerticalLayout();
        stats.setPadding(false);
        stats.setSpacing(false);

        HorizontalLayout numBadge = new HorizontalLayout();
        numBadge.setAlignItems(FlexComponent.Alignment.BASELINE);
        numBadge.setSpacing(true);

        aqiValueSpan.getStyle()
                .set("font-size", "28px")
                .set("font-weight", "500")
                .set("color", UiThemeUtils.COLOR_TEXT_PRIMARY)
                .set("font-variant-numeric", "tabular-nums");

        aqiBadgeSpan.getStyle()
                .set("font-size", "12px")
                .set("font-weight", "500")
                .set("padding", "2px 8px")
                .set("border-radius", "6px");

        numBadge.add(aqiValueSpan, aqiBadgeSpan);

        aqiDetailsSpan.getStyle()
                .set("font-size", "12px")
                .set("color", UiThemeUtils.COLOR_TEXT_MUTED)
                .set("margin-top", "6px")
                .set("font-variant-numeric", "tabular-nums");

        stats.add(numBadge, aqiDetailsSpan);
        body.add(stats, aqiArcMeterDiv);

        card.add(body);
        return card;
    }

    private Div buildUvCard() {
        Div card = buildBaseCard("UV radiation", SvgWeatherIcons.getUvIconSvg(16));

        HorizontalLayout body = new HorizontalLayout();
        body.setWidthFull();
        body.setAlignItems(FlexComponent.Alignment.CENTER);
        body.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        VerticalLayout stats = new VerticalLayout();
        stats.setPadding(false);
        stats.setSpacing(false);

        HorizontalLayout numBadge = new HorizontalLayout();
        numBadge.setAlignItems(FlexComponent.Alignment.BASELINE);
        numBadge.setSpacing(true);

        uvValueSpan.getStyle()
                .set("font-size", "28px")
                .set("font-weight", "500")
                .set("color", UiThemeUtils.COLOR_TEXT_PRIMARY)
                .set("font-variant-numeric", "tabular-nums");

        uvBadgeSpan.getStyle()
                .set("font-size", "12px")
                .set("font-weight", "500")
                .set("padding", "2px 8px")
                .set("border-radius", "6px");

        numBadge.add(uvValueSpan, uvBadgeSpan);

        uvAdviceSpan.getStyle()
                .set("font-size", "12px")
                .set("color", UiThemeUtils.COLOR_TEXT_MUTED)
                .set("margin-top", "6px");

        stats.add(numBadge, uvAdviceSpan);
        body.add(stats, uvArcMeterDiv);

        card.add(body);
        return card;
    }

    private Div buildWindCard() {
        Div card = buildBaseCard("Wind vector", SvgWeatherIcons.getWindIconSvg(16));

        HorizontalLayout body = new HorizontalLayout();
        body.setWidthFull();
        body.setAlignItems(FlexComponent.Alignment.CENTER);
        body.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);

        VerticalLayout stats = new VerticalLayout();
        stats.setPadding(false);
        stats.setSpacing(false);

        windSpeedSpan.getStyle()
                .set("font-size", "28px")
                .set("font-weight", "500")
                .set("color", UiThemeUtils.COLOR_TEXT_PRIMARY)
                .set("font-variant-numeric", "tabular-nums");

        windDirectionSpan.getStyle()
                .set("font-size", "13px")
                .set("font-weight", "400")
                .set("color", UiThemeUtils.COLOR_TEXT_MUTED)
                .set("margin-top", "4px");

        stats.add(windSpeedSpan, windDirectionSpan);
        body.add(stats, windCompassDiv);

        card.add(body);
        return card;
    }

    private Div buildHumidityCard() {
        Div card = buildBaseCard("Humidity & dew point", SvgWeatherIcons.getHumidityIconSvg(16));

        humidityValueSpan.getStyle()
                .set("font-size", "28px")
                .set("font-weight", "500")
                .set("color", UiThemeUtils.COLOR_TEXT_PRIMARY)
                .set("font-variant-numeric", "tabular-nums");

        dewPointSpan.getStyle()
                .set("font-size", "12px")
                .set("color", UiThemeUtils.COLOR_TEXT_MUTED)
                .set("margin-top", "6px")
                .set("font-variant-numeric", "tabular-nums");

        card.add(humidityValueSpan, dewPointSpan);
        return card;
    }

    private Div buildVisibilityCard() {
        Div card = buildBaseCard("Optical visibility", SvgWeatherIcons.getVisibilityIconSvg(16));

        visibilityValueSpan.getStyle()
                .set("font-size", "28px")
                .set("font-weight", "500")
                .set("color", UiThemeUtils.COLOR_TEXT_PRIMARY)
                .set("font-variant-numeric", "tabular-nums");

        visibilityRatingSpan.getStyle()
                .set("font-size", "12px")
                .set("color", UiThemeUtils.COLOR_TEXT_MUTED)
                .set("margin-top", "6px");

        card.add(visibilityValueSpan, visibilityRatingSpan);
        return card;
    }

    private Div buildPressureCard() {
        Div card = buildBaseCard("Barometric pressure", SvgWeatherIcons.getPressureIconSvg(16));

        pressureValueSpan.getStyle()
                .set("font-size", "28px")
                .set("font-weight", "500")
                .set("color", UiThemeUtils.COLOR_TEXT_PRIMARY)
                .set("font-variant-numeric", "tabular-nums");

        pressureTrendSpan.getStyle()
                .set("font-size", "12px")
                .set("font-weight", "400")
                .set("color", UiThemeUtils.COLOR_TEXT_MUTED)
                .set("margin-top", "6px");

        card.add(pressureValueSpan, pressureTrendSpan);
        return card;
    }

    public void update(WeatherReport report, UnitSystem unitSystem) {
        // 1. AQI
        AirQualityMetrics aqi = report.airQuality();
        aqiValueSpan.setText(String.valueOf(aqi.aqiUs()));
        aqiBadgeSpan.setText(aqi.category());
        aqiBadgeSpan.getStyle()
                .set("background", aqi.badgeColor() + "22")
                .set("color", aqi.badgeColor())
                .set("border", "1px solid " + aqi.badgeColor() + "44");

        aqiDetailsSpan.setText(String.format(Locale.US, "PM2.5 %.1f · PM10 %.1f · O₃ %.1f",
                aqi.pm25(), aqi.pm10(), aqi.ozone()));

        // Mini SVG circular meter
        double aqiRatio = Math.min(1.0, aqi.aqiUs() / 300.0);
        double strokeDash = aqiRatio * 125.6;
        aqiArcMeterDiv.removeAll();
        aqiArcMeterDiv.add(new Html(String.format(Locale.US, """
            <svg width="48" height="48" viewBox="0 0 50 50">
              <circle cx="25" cy="25" r="20" stroke="rgba(238,240,245,0.08)" stroke-width="3" fill="none"/>
              <circle cx="25" cy="25" r="20" stroke="%s" stroke-width="3" fill="none"
                      stroke-dasharray="125.6" stroke-dashoffset="%.1f" stroke-linecap="round"
                      transform="rotate(-90 25 25)"/>
            </svg>
            """, aqi.badgeColor(), 125.6 - strokeDash)));

        // 2. UV
        int uv = report.uvIndex();
        uvValueSpan.setText(String.valueOf(uv));
        String uvLevel;
        String uvColor;
        String uvAdvice;

        if (uv <= 2) {
            uvLevel = "Low";
            uvColor = UiThemeUtils.COLOR_POSITIVE;
            uvAdvice = "Minimal solar protection needed";
        } else if (uv <= 5) {
            uvLevel = "Moderate";
            uvColor = UiThemeUtils.COLOR_WARNING;
            uvAdvice = "Eye protection recommended";
        } else if (uv <= 7) {
            uvLevel = "High";
            uvColor = "#f97316";
            uvAdvice = "Sunscreen recommended";
        } else if (uv <= 10) {
            uvLevel = "Very high";
            uvColor = UiThemeUtils.COLOR_NEGATIVE;
            uvAdvice = "Limit midday exposure";
        } else {
            uvLevel = "Extreme";
            uvColor = "#c084fc";
            uvAdvice = "Seek shade";
        }

        uvBadgeSpan.setText(uvLevel);
        uvBadgeSpan.getStyle()
                .set("background", uvColor + "22")
                .set("color", uvColor)
                .set("border", "1px solid " + uvColor + "44");
        uvAdviceSpan.setText(uvAdvice);

        double uvRatio = Math.min(1.0, uv / 12.0);
        double uvDash = uvRatio * 125.6;
        uvArcMeterDiv.removeAll();
        uvArcMeterDiv.add(new Html(String.format(Locale.US, """
            <svg width="48" height="48" viewBox="0 0 50 50">
              <circle cx="25" cy="25" r="20" stroke="rgba(238,240,245,0.08)" stroke-width="3" fill="none"/>
              <circle cx="25" cy="25" r="20" stroke="%s" stroke-width="3" fill="none"
                      stroke-dasharray="125.6" stroke-dashoffset="%.1f" stroke-linecap="round"
                      transform="rotate(-90 25 25)"/>
            </svg>
            """, uvColor, 125.6 - uvDash)));

        // 3. Wind
        windSpeedSpan.setText(conversionService.formatSpeed(report.windSpeedKmh(), unitSystem));
        windDirectionSpan.setText(report.windCardinal() + " · " + report.windDirectionDegrees() + "°");
        windCompassDiv.removeAll();
        windCompassDiv.add(SvgWeatherIcons.getWindCompassSvg(report.windDirectionDegrees(), 56));

        // 4. Humidity & Dew Point
        humidityValueSpan.setText(report.humidity() + "%");
        dewPointSpan.setText("Dew point " + conversionService.formatTemperature(report.dewPoint(), unitSystem));

        // 5. Visibility
        visibilityValueSpan.setText(conversionService.formatDistance(report.visibilityKm(), unitSystem));
        String visDesc = report.visibilityKm() >= 15 ? "Clear optical range" :
                (report.visibilityKm() >= 8 ? "Moderate line of sight" : "Atmospheric haze");
        visibilityRatingSpan.setText(visDesc);

        // 6. Pressure
        pressureValueSpan.setText(conversionService.formatPressure(report.pressureHpa(), unitSystem));
        String pressTrend = (report.pressureHpa() >= 1013.25) ? "High pressure baseline" : "Low pressure trough";
        pressureTrendSpan.setText(pressTrend);
    }
}
