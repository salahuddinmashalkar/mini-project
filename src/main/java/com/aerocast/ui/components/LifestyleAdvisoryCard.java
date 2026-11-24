package com.aerocast.ui.components;

import com.aerocast.model.LifestyleAdvisory;
import com.aerocast.ui.util.UiThemeUtils;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.List;

/**
 * Lifestyle advisories and meteorological briefing panel.
 * Unslopped:
 * - Removed purple glows and gradients in favor of quiet neutral card surfaces
 * - Zero emojis in category titles
 * - Sentence case headings, restrained 8px nested item radius
 */
public class LifestyleAdvisoryCard extends Div {

        private final VerticalLayout advisoriesLayout = new VerticalLayout();
        private final Div aiBriefingContainer = new Div();
        private final Paragraph aiBriefingParagraph = new Paragraph();
        private String currentAiText = "";

        public LifestyleAdvisoryCard() {
                initLayout();
        }

        private void initLayout() {
                getStyle()
                                .set("padding", "20px")
                                .set("border-radius", "16px")
                                .set("background", UiThemeUtils.COLOR_SURFACE_CARD)
                                .set("border", "1px solid " + UiThemeUtils.COLOR_HAIRLINE)
                                .set("box-shadow", "inset 0 1px 0 rgba(255, 255, 255, 0.04)");

                H3 title = new H3("Atmospheric advisories");
                title.getStyle()
                                .set("margin", "0 0 16px 0")
                                .set("font-size", "15px")
                                .set("font-weight", "500")
                                .set("color", UiThemeUtils.COLOR_TEXT_PRIMARY);

                // Meteorological Briefing Box (quiet raised surface)
                aiBriefingContainer.getStyle()
                                .set("padding", "16px")
                                .set("border-radius", "8px")
                                .set("background", UiThemeUtils.COLOR_SURFACE_RAISED)
                                .set("border", "1px solid " + UiThemeUtils.COLOR_HAIRLINE)
                                .set("margin-bottom", "16px");

                HorizontalLayout aiHeader = new HorizontalLayout();
                aiHeader.setWidthFull();
                aiHeader.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
                aiHeader.setAlignItems(FlexComponent.Alignment.CENTER);
                aiHeader.getStyle().set("margin-bottom", "8px");

                HorizontalLayout aiTitleGroup = new HorizontalLayout();
                aiTitleGroup.setAlignItems(FlexComponent.Alignment.CENTER);
                aiTitleGroup.setSpacing(true);

                Span aiBadge = new Span("Meteorological briefing");
                aiBadge.getStyle()
                                .set("font-size", "13px")
                                .set("font-weight", "500")
                                .set("color", UiThemeUtils.COLOR_TEXT_PRIMARY);

        Span modelBadge = new Span("Groq AI");
        modelBadge.getStyle()
                .set("font-size", "11px")
                .set("font-family", "monospace")
                .set("padding", "2px 6px")
                .set("border-radius", "4px")
                .set("background", "rgba(238, 240, 245, 0.06)")
                .set("color", UiThemeUtils.COLOR_TEXT_MUTED);

                aiTitleGroup.add(aiBadge, modelBadge);

                Button copyBtn = new Button(VaadinIcon.COPY.create());
                copyBtn.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_SMALL);
                copyBtn.setTooltipText("Copy briefing");
                copyBtn.getStyle()
                                .set("color", UiThemeUtils.COLOR_TEXT_MUTED)
                                .set("padding", "0")
                                .set("min-width", "20px");
                copyBtn.addClickListener(e -> {
                        if (!currentAiText.isBlank()) {
                                UI.getCurrent().getPage().executeJs("navigator.clipboard.writeText($0)", currentAiText);
                                Notification.show("Briefing copied to clipboard", 2000,
                                                Notification.Position.BOTTOM_END);
                        }
                });

                aiHeader.add(aiTitleGroup, copyBtn);

                aiBriefingParagraph.getStyle()
                                .set("margin", "0")
                                .set("font-size", "13px")
                                .set("line-height", "1.5")
                                .set("color", UiThemeUtils.COLOR_TEXT_SECONDARY);

                aiBriefingContainer.add(aiHeader, aiBriefingParagraph);

                // Rule-based advisories container
                advisoriesLayout.setPadding(false);
                advisoriesLayout.setSpacing(true);
                advisoriesLayout.setWidthFull();

                add(title, aiBriefingContainer, advisoriesLayout);
        }

        public void update(List<LifestyleAdvisory> advisories, String aiBriefing) {
                if (aiBriefing != null && !aiBriefing.isBlank()) {
                        this.currentAiText = aiBriefing;
                        aiBriefingContainer.setVisible(true);
                        aiBriefingParagraph.setText(aiBriefing);
                } else {
                        aiBriefingContainer.setVisible(false);
                }

                advisoriesLayout.removeAll();
                if (advisories == null || advisories.isEmpty()) {
                        return;
                }

                for (LifestyleAdvisory adv : advisories) {
                        Div item = new Div();
                        item.setWidthFull();
                        item.getStyle()
                                        .set("padding", "12px 14px")
                                        .set("border-radius", "8px")
                                        .set("background", UiThemeUtils.COLOR_SURFACE_RAISED)
                                        .set("border", "1px solid " + UiThemeUtils.COLOR_HAIRLINE)
                                        .set("margin-bottom", "8px");

                        HorizontalLayout itemHeader = new HorizontalLayout();
                        itemHeader.setWidthFull();
                        itemHeader.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
                        itemHeader.setAlignItems(FlexComponent.Alignment.CENTER);

                        H4 catTitle = new H4(adv.title());
                        catTitle.getStyle()
                                        .set("margin", "0")
                                        .set("font-size", "13px")
                                        .set("font-weight", "500")
                                        .set("color", UiThemeUtils.COLOR_TEXT_PRIMARY);

                        Span badge = new Span(adv.badgeLevel());
                        badge.getStyle()
                                        .set("font-size", "11px")
                                        .set("font-weight", "500")
                                        .set("padding", "2px 6px")
                                        .set("border-radius", "4px")
                                        .set("background", adv.badgeColor() + "22")
                                        .set("color", adv.badgeColor())
                                        .set("border", "1px solid " + adv.badgeColor() + "44");

                        itemHeader.add(catTitle, badge);

                        Paragraph recP = new Paragraph(adv.recommendation());
                        recP.getStyle()
                                        .set("margin", "6px 0 0 0")
                                        .set("font-size", "12px")
                                        .set("line-height", "1.45")
                                        .set("color", UiThemeUtils.COLOR_TEXT_MUTED);

                        item.add(itemHeader, recP);
                        advisoriesLayout.add(item);
                }
        }
}
