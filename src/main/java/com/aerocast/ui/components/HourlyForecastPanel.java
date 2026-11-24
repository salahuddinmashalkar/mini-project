package com.aerocast.ui.components;

import com.aerocast.model.HourlyForecast;
import com.aerocast.model.UnitSystem;
import com.aerocast.service.UnitConversionService;
import com.aerocast.ui.util.SplineCurveGenerator;
import com.aerocast.ui.util.SvgWeatherIcons;
import com.aerocast.ui.util.UiThemeUtils;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 24-Hour meteorological trend panel.
 * Unslopped:
 * - Clean SVG trend curve without fluorescent glow filter
 * - Precise 4px rhythm and nested 10px card radius
 * - Tabular figures, sentence case typography
 */
public class HourlyForecastPanel extends Div {

    private final UnitConversionService conversionService;
    private final HorizontalLayout cardsContainer = new HorizontalLayout();
    private final Div svgChartContainer = new Div();

    public HourlyForecastPanel(UnitConversionService conversionService) {
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

        HorizontalLayout header = new HorizontalLayout();
        header.setWidthFull();
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.getStyle().set("margin-bottom", "16px");

        H3 title = new H3("Hourly trend");
        title.getStyle()
                .set("margin", "0")
                .set("font-size", "15px")
                .set("font-weight", "500")
                .set("color", UiThemeUtils.COLOR_TEXT_PRIMARY);

        Span legend = new Span("24-hour temperature and precipitation");
        legend.getStyle()
                .set("font-size", "12px")
                .set("color", UiThemeUtils.COLOR_TEXT_MUTED)
                .set("font-weight", "400");

        header.add(title, legend);

        svgChartContainer.setWidthFull();
        svgChartContainer.getStyle().set("margin-bottom", "16px");

        cardsContainer.setWidthFull();
        cardsContainer.setSpacing(true);
        cardsContainer.getStyle()
                .set("overflow-x", "auto")
                .set("padding-bottom", "8px")
                .set("scrollbar-width", "none");

        add(header, svgChartContainer, cardsContainer);
    }

    public void update(List<HourlyForecast> hourlyList, UnitSystem unitSystem) {
        cardsContainer.removeAll();
        svgChartContainer.removeAll();

        if (hourlyList == null || hourlyList.isEmpty()) {
            return;
        }

        renderCubicSpline(hourlyList, unitSystem);
        renderHourlyCards(hourlyList, unitSystem);
    }

    private void renderCubicSpline(List<HourlyForecast> list, UnitSystem unitSystem) {
        int count = Math.min(list.size(), 24);
        double minT = Double.MAX_VALUE;
        double maxT = Double.MIN_VALUE;

        for (int i = 0; i < count; i++) {
            double t = conversionService.convertTemperature(list.get(i).temperature(), unitSystem);
            if (t < minT) minT = t;
            if (t > maxT) maxT = t;
        }

        if (maxT == minT) {
            maxT += 1.0;
            minT -= 1.0;
        }

        double width = 800.0;
        double height = 72.0;
        double padY = 16.0;

        List<SplineCurveGenerator.Point> points = new ArrayList<>();
        int maxIndex = 0;
        int minIndex = 0;

        for (int i = 0; i < count; i++) {
            double t = conversionService.convertTemperature(list.get(i).temperature(), unitSystem);
            if (t == maxT) maxIndex = i;
            if (t == minT) minIndex = i;

            double x = (width / (count - 1)) * i;
            double norm = (t - minT) / (maxT - minT);
            double y = height - padY - (norm * (height - (2 * padY)));
            points.add(new SplineCurveGenerator.Point(x, y));
        }

        String smoothPath = SplineCurveGenerator.generateSmoothPath(points);
        String areaPath = smoothPath + String.format(Locale.US, " L %.1f,%.1f L 0,%.1f Z", width, height, height);

        SplineCurveGenerator.Point maxPt = points.get(maxIndex);
        SplineCurveGenerator.Point minPt = points.get(minIndex);

        String svgHtml = String.format(Locale.US, """
            <svg viewBox="0 0 %.0f %.0f" width="100%%" height="72" xmlns="http://www.w3.org/2000/svg" preserveAspectRatio="none">
              <defs>
                <linearGradient id="splineAreaGrad" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="0%%" stop-color="#38bdf8" stop-opacity="0.14"/>
                  <stop offset="100%%" stop-color="#38bdf8" stop-opacity="0.0"/>
                </linearGradient>
              </defs>
              <path d="%s" fill="url(#splineAreaGrad)"/>
              <path d="%s" fill="none" stroke="#38bdf8" stroke-width="1.8" stroke-linecap="round"/>
              <circle cx="%.1f" cy="%.1f" r="3" fill="#f59e0b" stroke="#11141c" stroke-width="1.5"/>
              <circle cx="%.1f" cy="%.1f" r="3" fill="#38bdf8" stroke="#11141c" stroke-width="1.5"/>
            </svg>
            """, width, height, areaPath, smoothPath, maxPt.x(), maxPt.y(), minPt.x(), minPt.y());

        svgChartContainer.add(new Html(svgHtml));
    }

    private void renderHourlyCards(List<HourlyForecast> list, UnitSystem unitSystem) {
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("h a");

        for (int i = 0; i < list.size(); i++) {
            HourlyForecast h = list.get(i);
            String timeStr = (i == 0) ? "Now" : h.time().format(timeFmt);

            VerticalLayout card = new VerticalLayout();
            card.setAlignItems(FlexComponent.Alignment.CENTER);
            card.setSpacing(false);
            card.setPadding(false);
            card.getStyle()
                    .set("min-width", "80px")
                    .set("padding", "12px 8px")
                    .set("border-radius", "10px")
                    .set("background", (i == 0) ? UiThemeUtils.COLOR_SURFACE_RAISED : "transparent")
                    .set("border", (i == 0) ? "1px solid rgba(56, 189, 248, 0.3)" : "1px solid " + UiThemeUtils.COLOR_HAIRLINE_SUBTLE);

            Span timeLbl = new Span(timeStr);
            timeLbl.getStyle()
                    .set("font-size", "12px")
                    .set("font-weight", (i == 0) ? "500" : "400")
                    .set("color", (i == 0) ? UiThemeUtils.COLOR_ACCENT : UiThemeUtils.COLOR_TEXT_MUTED)
                    .set("margin-bottom", "6px");

            Div iconDiv = new Div(SvgWeatherIcons.getConditionIcon(h.condition(), 28, h.isDaytime()));
            iconDiv.getStyle().set("margin-bottom", "6px");

            Span tempLbl = new Span(conversionService.formatTemperature(h.temperature(), unitSystem));
            tempLbl.getStyle()
                    .set("font-size", "14px")
                    .set("font-weight", "500")
                    .set("color", UiThemeUtils.COLOR_TEXT_PRIMARY)
                    .set("margin-bottom", "4px")
                    .set("font-variant-numeric", "tabular-nums");

            Span rainLbl = new Span(h.precipitationProbability() + "%");
            rainLbl.getStyle()
                    .set("font-size", "11px")
                    .set("font-weight", "400")
                    .set("color", (h.precipitationProbability() > 40) ? UiThemeUtils.COLOR_ACCENT : UiThemeUtils.COLOR_TEXT_MUTED)
                    .set("font-variant-numeric", "tabular-nums");

            card.add(timeLbl, iconDiv, tempLbl, rainLbl);
            cardsContainer.add(card);
        }
    }
}
