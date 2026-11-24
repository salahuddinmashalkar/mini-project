package com.aerocast.ui.components;

import com.aerocast.model.UnitSystem;
import com.aerocast.model.WeatherReport;
import com.aerocast.service.UnitConversionService;
import com.aerocast.ui.util.SvgWeatherIcons;
import com.aerocast.ui.util.UiThemeUtils;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

/**
 * Weather observation hero card.
 * Refactored using unslop craft rules:
 * - Quiet neutral ramp surface with subtle vertical tone shift
 * - Strict 4px spacing (24px card padding, 16px radius, 8px controls)
 * - Clear typographic hierarchy (weights 400 and 500, tabular figures)
 * - Zero glowing orbs, zero rainbow drop-shadows
 */
public class HeroWeatherCard extends Div {

    private final UnitConversionService conversionService;
    private final Consumer<String> onFavoriteToggle;

    private final H1 cityNameHeading = new H1();
    private final Span countrySpan = new Span();
    private final Span timeSpan = new Span();
    private final Span tempMainSpan = new Span();
    private final Span feelsLikeSpan = new Span();
    private final Span conditionDescSpan = new Span();
    private final Span highLowCapsule = new Span();
    private final Div iconContainer = new Div();
    private final Button favoriteBtn = new Button();
    private final Span liveIndicatorBadge = new Span();

    private WeatherReport currentReport;

    public HeroWeatherCard(UnitConversionService conversionService, Consumer<String> onFavoriteToggle) {
        this.conversionService = conversionService;
        this.onFavoriteToggle = onFavoriteToggle;

        initLayout();
    }

    private void initLayout() {
        getStyle()
                .set("position", "relative")
                .set("padding", "24px")
                .set("border-radius", "16px")
                .set("background", UiThemeUtils.COLOR_SURFACE_CARD)
                .set("border", "1px solid " + UiThemeUtils.COLOR_HAIRLINE)
                .set("box-shadow", "inset 0 1px 0 rgba(255, 255, 255, 0.04)")
                .set("transition", "border-color 150ms ease, background 150ms ease");

        VerticalLayout contentStack = new VerticalLayout();
        contentStack.setPadding(false);
        contentStack.setSpacing(false);
        contentStack.setWidthFull();

        // Top Row: Location & Station Status
        HorizontalLayout topBar = new HorizontalLayout();
        topBar.setWidthFull();
        topBar.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        topBar.setAlignItems(FlexComponent.Alignment.CENTER);

        HorizontalLayout locationInfo = new HorizontalLayout();
        locationInfo.setAlignItems(FlexComponent.Alignment.BASELINE);
        locationInfo.setSpacing(true);

        cityNameHeading.getStyle()
                .set("margin", "0")
                .set("font-size", "28px")
                .set("font-weight", "500")
                .set("color", UiThemeUtils.COLOR_TEXT_PRIMARY)
                .set("letter-spacing", "-0.02em");

        countrySpan.getStyle()
                .set("font-size", "14px")
                .set("font-weight", "400")
                .set("color", UiThemeUtils.COLOR_TEXT_MUTED)
                .set("margin-left", "8px");

        locationInfo.add(cityNameHeading, countrySpan);

        HorizontalLayout actionControls = new HorizontalLayout();
        actionControls.setAlignItems(FlexComponent.Alignment.CENTER);
        actionControls.setSpacing(true);

        liveIndicatorBadge.getStyle()
                .set("font-size", "12px")
                .set("font-weight", "500")
                .set("padding", "4px 8px")
                .set("border-radius", "6px")
                .set("display", "inline-flex")
                .set("align-items", "center")
                .set("gap", "6px");

        favoriteBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_ICON);
        favoriteBtn.getStyle()
                .set("cursor", "pointer")
                .set("font-size", "16px")
                .set("color", UiThemeUtils.COLOR_TEXT_MUTED)
                .set("padding", "4px");
        favoriteBtn.addClickListener(e -> {
            if (currentReport != null) {
                onFavoriteToggle.accept(currentReport.cityName());
            }
        });

        actionControls.add(liveIndicatorBadge, favoriteBtn);
        topBar.add(locationInfo, actionControls);

        // Center: Primary Temperature Metric & Condition Vector
        HorizontalLayout mainContent = new HorizontalLayout();
        mainContent.setWidthFull();
        mainContent.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        mainContent.setAlignItems(FlexComponent.Alignment.CENTER);
        mainContent.getStyle().set("margin-top", "20px");

        VerticalLayout tempCol = new VerticalLayout();
        tempCol.setPadding(false);
        tempCol.setSpacing(false);

        tempMainSpan.getStyle()
                .set("font-size", "64px")
                .set("font-weight", "500")
                .set("line-height", "1")
                .set("color", UiThemeUtils.COLOR_TEXT_PRIMARY)
                .set("letter-spacing", "-0.03em")
                .set("font-variant-numeric", "tabular-nums");

        HorizontalLayout subMetrics = new HorizontalLayout();
        subMetrics.setAlignItems(FlexComponent.Alignment.CENTER);
        subMetrics.setSpacing(true);
        subMetrics.getStyle().set("margin-top", "8px");

        feelsLikeSpan.getStyle()
                .set("font-size", "14px")
                .set("color", UiThemeUtils.COLOR_TEXT_SECONDARY)
                .set("font-weight", "400")
                .set("font-variant-numeric", "tabular-nums");

        highLowCapsule.getStyle()
                .set("font-size", "12px")
                .set("font-weight", "500")
                .set("color", UiThemeUtils.COLOR_TEXT_SECONDARY)
                .set("background", UiThemeUtils.COLOR_SURFACE_RAISED)
                .set("padding", "3px 8px")
                .set("border-radius", "6px")
                .set("border", "1px solid " + UiThemeUtils.COLOR_HAIRLINE)
                .set("font-variant-numeric", "tabular-nums");

        subMetrics.add(feelsLikeSpan, highLowCapsule);

        conditionDescSpan.getStyle()
                .set("font-size", "16px")
                .set("font-weight", "500")
                .set("color", UiThemeUtils.COLOR_TEXT_PRIMARY)
                .set("margin-top", "12px");

        timeSpan.getStyle()
                .set("font-size", "12px")
                .set("color", UiThemeUtils.COLOR_TEXT_MUTED)
                .set("margin-top", "4px");

        tempCol.add(tempMainSpan, subMetrics, conditionDescSpan, timeSpan);

        iconContainer.getStyle()
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center");

        mainContent.add(tempCol, iconContainer);

        contentStack.add(topBar, mainContent);
        add(contentStack);
    }

    public void update(WeatherReport report, UnitSystem unitSystem, boolean isFavorited) {
        this.currentReport = report;

        cityNameHeading.setText(report.cityName());
        countrySpan.setText(report.country());

        tempMainSpan.setText(conversionService.formatTemperature(report.currentTemp(), unitSystem));
        feelsLikeSpan.setText("Feels like " + conversionService.formatTemperature(report.feelsLikeTemp(), unitSystem));
        highLowCapsule.setText("High " + conversionService.formatTemperature(report.tempMax(), unitSystem) +
                "  ·  Low " + conversionService.formatTemperature(report.tempMin(), unitSystem));

        conditionDescSpan.setText(report.conditionDescription());

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EEEE, MMM d · h:mm a");
        timeSpan.setText("Observed " + report.observationTime().format(dtf) + " · " + report.dataSourceDescription());

        // Weather Icon (clean, 112px)
        iconContainer.removeAll();
        iconContainer.add(SvgWeatherIcons.getConditionIcon(report.condition(), 112, report.isDaytime()));

        // Favorite Button Icon
        if (isFavorited) {
            favoriteBtn.setIcon(VaadinIcon.STAR.create());
            favoriteBtn.setTooltipText("Remove from pinned locations");
            favoriteBtn.getStyle().set("color", "#f59e0b");
        } else {
            favoriteBtn.setIcon(VaadinIcon.STAR_O.create());
            favoriteBtn.setTooltipText("Pin to quick bar");
            favoriteBtn.getStyle().set("color", UiThemeUtils.COLOR_TEXT_MUTED);
        }

        // Live vs Sample Data Mode Badge: quiet indicator tag
        if (report.isLiveApi()) {
            liveIndicatorBadge.setText("Live broadcast");
            liveIndicatorBadge.getStyle()
                    .set("background", "rgba(76, 195, 138, 0.12)")
                    .set("color", UiThemeUtils.COLOR_POSITIVE)
                    .set("border", "1px solid rgba(76, 195, 138, 0.2)");
        } else {
            liveIndicatorBadge.setText("Observation sample");
            liveIndicatorBadge.getStyle()
                    .set("background", UiThemeUtils.COLOR_SURFACE_RAISED)
                    .set("color", UiThemeUtils.COLOR_TEXT_MUTED)
                    .set("border", "1px solid " + UiThemeUtils.COLOR_HAIRLINE);
        }

        // Subtle atmospheric background gradient and quiet hairline
        String gradient = UiThemeUtils.getHeroGradient(report.condition(), report.isDaytime());
        String borderColor = UiThemeUtils.getHeroBorderColor(report.condition());

        getStyle()
                .set("background", gradient)
                .set("border", "1px solid " + borderColor);
    }
}
