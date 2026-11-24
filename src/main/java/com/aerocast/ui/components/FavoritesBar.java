package com.aerocast.ui.components;

import com.aerocast.model.FavoriteCityEntity;
import com.aerocast.ui.util.UiThemeUtils;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import java.util.List;
import java.util.function.Consumer;

/**
 * Pinned locations bar.
 * Follows unslop guidelines:
 * - Nested radius (8px chip radius)
 * - Quiet hairline borders with clean surface step for active state (no neon glow)
 * - Sentence case labels, 4px grid rhythm
 */
public class FavoritesBar extends Div {

    private final Consumer<String> onCitySelected;
    private final Consumer<String> onCityRemoved;
    private final HorizontalLayout chipsLayout = new HorizontalLayout();

    public FavoritesBar(Consumer<String> onCitySelected, Consumer<String> onCityRemoved) {
        this.onCitySelected = onCitySelected;
        this.onCityRemoved = onCityRemoved;

        setWidthFull();
        getStyle().set("margin-bottom", "16px");

        chipsLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        chipsLayout.setSpacing(true);
        chipsLayout.getStyle()
                .set("overflow-x", "auto")
                .set("padding-bottom", "4px")
                .set("scrollbar-width", "none");

        add(chipsLayout);
    }

    public void updateFavorites(List<FavoriteCityEntity> favorites, String currentCity) {
        chipsLayout.removeAll();

        if (favorites.isEmpty()) {
            Span emptyHint = new Span("No pinned locations. Click the star icon to pin a city.");
            emptyHint.getStyle()
                    .set("font-size", "13px")
                    .set("color", UiThemeUtils.COLOR_TEXT_MUTED);
            chipsLayout.add(emptyHint);
            return;
        }

        Span label = new Span("Pinned");
        label.getStyle()
                .set("font-size", "12px")
                .set("font-weight", "500")
                .set("color", UiThemeUtils.COLOR_TEXT_MUTED)
                .set("margin-right", "4px");
        chipsLayout.add(label);

        for (FavoriteCityEntity fav : favorites) {
            boolean isActive = fav.getCityName().equalsIgnoreCase(currentCity);

            HorizontalLayout chip = new HorizontalLayout();
            chip.setAlignItems(FlexComponent.Alignment.CENTER);
            chip.setSpacing(false);
            chip.getStyle()
                    .set("cursor", "pointer")
                    .set("padding", "4px 10px")
                    .set("border-radius", "8px")
                    .set("transition", "background-color 150ms ease, border-color 150ms ease")
                    .set("user-select", "none");

            if (isActive) {
                chip.getStyle()
                        .set("background", UiThemeUtils.COLOR_SURFACE_RAISED)
                        .set("border", "1px solid rgba(56, 189, 248, 0.4)");
            } else {
                chip.getStyle()
                        .set("background", UiThemeUtils.COLOR_SURFACE_CARD)
                        .set("border", "1px solid " + UiThemeUtils.COLOR_HAIRLINE);
            }

            Span nameSpan = new Span(fav.getCityName());
            nameSpan.getStyle()
                    .set("font-size", "13px")
                    .set("font-weight", isActive ? "500" : "400")
                    .set("color", isActive ? UiThemeUtils.COLOR_ACCENT : UiThemeUtils.COLOR_TEXT_SECONDARY);
            nameSpan.addClickListener(e -> onCitySelected.accept(fav.getCityName()));

            Button removeBtn = new Button("×");
            removeBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
            removeBtn.getStyle()
                    .set("margin-left", "6px")
                    .set("padding", "0")
                    .set("min-width", "16px")
                    .set("height", "16px")
                    .set("line-height", "16px")
                    .set("color", UiThemeUtils.COLOR_TEXT_MUTED)
                    .set("font-size", "14px")
                    .set("cursor", "pointer");
            removeBtn.addClickListener(e -> onCityRemoved.accept(fav.getCityName()));

            chip.add(nameSpan, removeBtn);
            chipsLayout.add(chip);
        }
    }
}
