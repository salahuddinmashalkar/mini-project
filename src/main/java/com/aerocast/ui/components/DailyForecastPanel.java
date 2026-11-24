package com.aerocast.ui.components;

import com.aerocast.model.DailyForecast;
import com.aerocast.model.UnitSystem;
import com.aerocast.service.UnitConversionService;
import com.aerocast.ui.util.SvgWeatherIcons;
import com.aerocast.ui.util.UiThemeUtils;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.List;
import java.util.Locale;

/**
 * 7-Day extended forecast component.
 * Unslopped:
 * - Nested 8px row radius inside 16px card
 * - Clean 4px thermal range bars with tabular figures
 * - Consistent 36px row heights and sentence case typography
 */
public class DailyForecastPanel extends Div {

    private final UnitConversionService conversionService;
    private final VerticalLayout rowsContainer = new VerticalLayout();

    public DailyForecastPanel(UnitConversionService conversionService) {
        this.conversionService = conversionService;

        initLayout();
    }

    private void initLayout() {
        getStyle()
                .set("padding", "20px")
                .set("border-radius", "16px")
                .set("background", UiThemeUtils.COLOR_SURFACE_CARD)
                .set("border", "1px solid " + UiThemeUtils.COLOR_HAIRLINE)
                .set("box-shadow", "inset 0 1px 0 rgba(255, 255, 255, 0.04)");

        H3 title = new H3("7-Day forecast");
        title.getStyle()
                .set("margin", "0 0 16px 0")
                .set("font-size", "15px")
                .set("font-weight", "500")
                .set("color", UiThemeUtils.COLOR_TEXT_PRIMARY);

        rowsContainer.setPadding(false);
        rowsContainer.setSpacing(true);
        rowsContainer.setWidthFull();

        add(title, rowsContainer);
    }

    public void update(List<DailyForecast> dailyList, UnitSystem unitSystem) {
        rowsContainer.removeAll();

        if (dailyList == null || dailyList.isEmpty()) {
            return;
        }

        // Calculate global min and max across all 7 days to anchor the thermal scale
        double globalMin = Double.MAX_VALUE;
        double globalMax = Double.MIN_VALUE;

        for (DailyForecast d : dailyList) {
            double min = conversionService.convertTemperature(d.tempMin(), unitSystem);
            double max = conversionService.convertTemperature(d.tempMax(), unitSystem);
            if (min < globalMin) globalMin = min;
            if (max > globalMax) globalMax = max;
        }

        if (globalMax == globalMin) {
            globalMax += 1.0;
        }

        for (int i = 0; i < dailyList.size(); i++) {
            DailyForecast d = dailyList.get(i);
            boolean isToday = (i == 0);

            HorizontalLayout row = new HorizontalLayout();
            row.setWidthFull();
            row.setAlignItems(FlexComponent.Alignment.CENTER);
            row.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
            row.getStyle()
                    .set("padding", "8px 12px")
                    .set("border-radius", "8px")
                    .set("background", isToday ? UiThemeUtils.COLOR_SURFACE_RAISED : "transparent")
                    .set("border", isToday ? "1px solid rgba(56, 189, 248, 0.25)" : "1px solid transparent")
                    .set("margin-bottom", "4px")
                    .set("transition", "background-color 150ms ease");

            // Day Name
            Span daySpan = new Span(d.dayOfWeek());
            daySpan.getStyle()
                    .set("width", "64px")
                    .set("font-size", "13px")
                    .set("font-weight", isToday ? "500" : "400")
                    .set("color", isToday ? UiThemeUtils.COLOR_ACCENT : UiThemeUtils.COLOR_TEXT_PRIMARY);

            // Weather Icon & Rain Probability
            HorizontalLayout iconCol = new HorizontalLayout();
            iconCol.setAlignItems(FlexComponent.Alignment.CENTER);
            iconCol.setSpacing(true);
            iconCol.setWidth("64px");

            Div icon = new Div(SvgWeatherIcons.getConditionIcon(d.condition(), 22, true));
            Span rainProb = new Span(d.precipitationProbability() > 0 ? d.precipitationProbability() + "%" : "");
            rainProb.getStyle()
                    .set("font-size", "11px")
                    .set("font-weight", "400")
                    .set("color", UiThemeUtils.COLOR_TEXT_MUTED)
                    .set("font-variant-numeric", "tabular-nums");

            iconCol.add(icon, rainProb);

            // Min Temp
            double minVal = conversionService.convertTemperature(d.tempMin(), unitSystem);
            double maxVal = conversionService.convertTemperature(d.tempMax(), unitSystem);

            Span minSpan = new Span(String.format(Locale.US, "%.0f°", minVal));
            minSpan.getStyle()
                    .set("width", "32px")
                    .set("text-align", "right")
                    .set("font-size", "13px")
                    .set("font-weight", "400")
                    .set("color", UiThemeUtils.COLOR_TEXT_MUTED)
                    .set("font-variant-numeric", "tabular-nums");

            // Proportional Thermal Range Bar (4px height)
            Div barTrack = new Div();
            barTrack.getStyle()
                    .set("flex-grow", "1")
                    .set("height", "4px")
                    .set("background", "rgba(238, 240, 245, 0.08)")
                    .set("border-radius", "2px")
                    .set("position", "relative")
                    .set("margin", "0 12px")
                    .set("overflow", "hidden");

            double leftPercent = Math.max(0, ((minVal - globalMin) / (globalMax - globalMin)) * 100.0);
            double rightPercent = Math.min(100, ((maxVal - globalMin) / (globalMax - globalMin)) * 100.0);
            double barWidth = Math.max(6, rightPercent - leftPercent);

            Div barSegment = new Div();
            barSegment.getStyle()
                    .set("position", "absolute")
                    .set("left", String.format(Locale.US, "%.1f%%", leftPercent))
                    .set("width", String.format(Locale.US, "%.1f%%", barWidth))
                    .set("height", "100%")
                    .set("border-radius", "2px")
                    .set("background", "linear-gradient(90deg, #38bdf8 0%, #f59e0b 100%)");

            barTrack.add(barSegment);

            // Max Temp
            Span maxSpan = new Span(String.format(Locale.US, "%.0f°", maxVal));
            maxSpan.getStyle()
                    .set("width", "32px")
                    .set("text-align", "left")
                    .set("font-size", "13px")
                    .set("font-weight", "500")
                    .set("color", UiThemeUtils.COLOR_TEXT_PRIMARY)
                    .set("font-variant-numeric", "tabular-nums");

            row.add(daySpan, iconCol, minSpan, barTrack, maxSpan);
            rowsContainer.add(row);
        }
    }
}
