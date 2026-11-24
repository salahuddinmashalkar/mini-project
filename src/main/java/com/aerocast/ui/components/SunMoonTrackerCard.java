package com.aerocast.ui.components;

import com.aerocast.model.AstronomyData;
import com.aerocast.ui.util.SvgWeatherIcons;
import com.aerocast.ui.util.UiThemeUtils;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.time.format.DateTimeFormatter;

/**
 * Sun and moon tracker component.
 * Unslopped:
 * - Clean SVG solar arc without emojis
 * - Sentence case titles and 500-weight figures
 * - 16px card radius on 4px grid
 */
public class SunMoonTrackerCard extends Div {

    private final Div solarArcContainer = new Div();
    private final Span daylightDurationSpan = new Span();
    private final Div moonGraphicContainer = new Div();
    private final Span moonPhaseNameSpan = new Span();
    private final Span moonIlluminationSpan = new Span();

    public SunMoonTrackerCard() {
        initLayout();
    }

    private void initLayout() {
        setWidthFull();
        getStyle()
                .set("display", "grid")
                .set("grid-template-columns", "repeat(auto-fit, minmax(280px, 1fr))")
                .set("gap", "16px");

        add(buildSunCard());
        add(buildMoonCard());
    }

    private Div buildSunCard() {
        Div card = new Div();
        card.getStyle()
                .set("padding", "20px")
                .set("border-radius", "16px")
                .set("background", UiThemeUtils.COLOR_SURFACE_CARD)
                .set("border", "1px solid " + UiThemeUtils.COLOR_HAIRLINE)
                .set("box-shadow", "inset 0 1px 0 rgba(255, 255, 255, 0.04)");

        HorizontalLayout header = new HorizontalLayout();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setSpacing(true);
        header.getStyle().set("margin-bottom", "8px");

        Div iconBox = new Div(SvgWeatherIcons.getUvIconSvg(16));
        H4 title = new H4("Solar cycle");
        title.getStyle()
                .set("margin", "0")
                .set("font-size", "13px")
                .set("font-weight", "500")
                .set("color", UiThemeUtils.COLOR_TEXT_SECONDARY);

        header.add(iconBox, title);

        solarArcContainer.setWidthFull();
        solarArcContainer.getStyle().set("margin", "8px 0");

        daylightDurationSpan.getStyle()
                .set("font-size", "12px")
                .set("font-weight", "400")
                .set("color", UiThemeUtils.COLOR_TEXT_MUTED)
                .set("font-variant-numeric", "tabular-nums");

        card.add(header, solarArcContainer, daylightDurationSpan);
        return card;
    }

    private Div buildMoonCard() {
        Div card = new Div();
        card.getStyle()
                .set("padding", "20px")
                .set("border-radius", "16px")
                .set("background", UiThemeUtils.COLOR_SURFACE_CARD)
                .set("border", "1px solid " + UiThemeUtils.COLOR_HAIRLINE)
                .set("box-shadow", "inset 0 1px 0 rgba(255, 255, 255, 0.04)");

        HorizontalLayout header = new HorizontalLayout();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setSpacing(true);
        header.getStyle().set("margin-bottom", "12px");

        H4 title = new H4("Lunar phase");
        title.getStyle()
                .set("margin", "0")
                .set("font-size", "13px")
                .set("font-weight", "500")
                .set("color", UiThemeUtils.COLOR_TEXT_SECONDARY);

        header.add(title);

        HorizontalLayout body = new HorizontalLayout();
        body.setWidthFull();
        body.setAlignItems(FlexComponent.Alignment.CENTER);
        body.setSpacing(true);

        moonGraphicContainer.getStyle().set("margin-right", "12px");

        VerticalLayout info = new VerticalLayout();
        info.setPadding(false);
        info.setSpacing(false);

        moonPhaseNameSpan.getStyle()
                .set("font-size", "16px")
                .set("font-weight", "500")
                .set("color", UiThemeUtils.COLOR_TEXT_PRIMARY);

        moonIlluminationSpan.getStyle()
                .set("font-size", "12px")
                .set("color", UiThemeUtils.COLOR_TEXT_MUTED)
                .set("margin-top", "4px")
                .set("font-variant-numeric", "tabular-nums");

        info.add(moonPhaseNameSpan, moonIlluminationSpan);
        body.add(moonGraphicContainer, info);

        card.add(header, body);
        return card;
    }

    public void update(AstronomyData astronomy) {
        if (astronomy == null) return;

        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("h:mm a");
        String sunriseStr = astronomy.sunrise().format(timeFmt);
        String sunsetStr = astronomy.sunset().format(timeFmt);

        solarArcContainer.removeAll();
        solarArcContainer.add(SvgWeatherIcons.getSolarArcSvg(astronomy.sunPositionPercent(), sunriseStr, sunsetStr));

        daylightDurationSpan.setText("Daylight " + astronomy.daylightDuration() +
                (astronomy.isSunUp() ? " · Sun is above horizon" : " · Nighttime interval"));

        moonGraphicContainer.removeAll();
        moonGraphicContainer.add(SvgWeatherIcons.getMoonPhaseSvg(astronomy.moonIlluminationPercent(), astronomy.moonPhase(), 48));

        moonPhaseNameSpan.setText(astronomy.moonPhase());
        moonIlluminationSpan.setText(astronomy.moonIlluminationPercent() + "% illumination");
    }
}
