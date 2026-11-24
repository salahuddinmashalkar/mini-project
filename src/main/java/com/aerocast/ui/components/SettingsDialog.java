package com.aerocast.ui.components;

import com.aerocast.model.UnitSystem;
import com.aerocast.service.UserPreferenceService;
import com.aerocast.ui.util.UiThemeUtils;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.PasswordField;

import java.util.Optional;

/**
 * System settings and API credential management dialog.
 * Security & Privacy:
 * - Plaintext API keys are NEVER sent to the browser DOM.
 * - Shows masked status indicators (e.g. "•••••••• (Active)") without exposing secrets.
 * - Password reveal button is strictly disabled.
 * - Leaving fields blank preserves the active keys securely.
 */
public class SettingsDialog extends Dialog {

    private final UserPreferenceService preferenceService;
    private final Runnable onSettingsSaved;

    private final PasswordField groqApiKeyField = new PasswordField("Groq API Key");
    private final PasswordField openWeatherKeyField = new PasswordField("OpenWeatherMap API Key (optional)");
    private final Select<UnitSystem> unitSelect = new Select<>();

    public SettingsDialog(UserPreferenceService preferenceService, Runnable onSettingsSaved) {
        this.preferenceService = preferenceService;
        this.onSettingsSaved = onSettingsSaved;

        setHeaderTitle("Settings");
        setWidth("480px");
        initContent();
    }

    private void initContent() {
        VerticalLayout content = new VerticalLayout();
        content.setPadding(true);
        content.setSpacing(true);
        content.setWidthFull();

        Paragraph info = new Paragraph("API credentials are stored securely on the server and are never exposed in plaintext to the browser.");
        info.getStyle()
                .set("font-size", "13px")
                .set("color", UiThemeUtils.COLOR_TEXT_SECONDARY)
                .set("margin", "0 0 12px 0");

        // 1. Groq API Key Input
        groqApiKeyField.setWidthFull();
        groqApiKeyField.setRevealButtonVisible(false); // Security: Never allow unmasking to plaintext
        groqApiKeyField.setClearButtonVisible(true);

        Optional<String> activeGroqKey = preferenceService.getGroqApiKey();
        if (activeGroqKey.isPresent() && !activeGroqKey.get().isBlank()) {
            String maskedHint = maskKey(activeGroqKey.get());
            groqApiKeyField.setPlaceholder("•••••••••••••••• (Key active: " + maskedHint + ")");
            groqApiKeyField.setHelperText("Active. Leave blank to keep current key, or enter a new one to replace it.");
        } else {
            groqApiKeyField.setPlaceholder("gsk_...");
            groqApiKeyField.setHelperText("Not configured. Enter your Groq API key here.");
        }
        // Strictly do NOT setValue() with the plaintext key to keep it out of browser DOM

        // 2. OpenWeatherMap Key Input
        openWeatherKeyField.setWidthFull();
        openWeatherKeyField.setRevealButtonVisible(false);
        openWeatherKeyField.setClearButtonVisible(true);

        Optional<String> activeOwmKey = preferenceService.getOpenWeatherApiKey();
        if (activeOwmKey.isPresent() && !activeOwmKey.get().isBlank()) {
            String maskedHint = maskKey(activeOwmKey.get());
            openWeatherKeyField.setPlaceholder("•••••••••••••••• (Key active: " + maskedHint + ")");
            openWeatherKeyField.setHelperText("Active. Leave blank to keep current key, or enter a new one to replace it.");
        } else {
            openWeatherKeyField.setPlaceholder("Optional API key");
            openWeatherKeyField.setHelperText("Optional secondary forecast provider.");
        }

        // 3. Default Unit System
        unitSelect.setLabel("Unit system");
        unitSelect.setItems(UnitSystem.values());
        unitSelect.setItemLabelGenerator(
                u -> u == UnitSystem.METRIC ? "Metric (°C, km/h, hPa)" : "Imperial (°F, mph, inHg)");
        unitSelect.setValue(preferenceService.getUnitSystem());
        unitSelect.setWidthFull();

        content.add(info, groqApiKeyField, openWeatherKeyField, unitSelect);
        add(content);

        // Buttons
        Button cancelBtn = new Button("Cancel", e -> close());
        Button saveBtn = new Button("Save settings", e -> saveSettings());
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        getFooter().add(cancelBtn, saveBtn);
    }

    private void saveSettings() {
        String newGroqKey = groqApiKeyField.getValue();
        if (newGroqKey != null && !newGroqKey.trim().isEmpty()) {
            // User typed a new key
            preferenceService.setGroqApiKey(newGroqKey.trim());
        }
        // If left empty, the existing key in .env or database is preserved securely

        String newOwmKey = openWeatherKeyField.getValue();
        if (newOwmKey != null && !newOwmKey.trim().isEmpty()) {
            preferenceService.setOpenWeatherApiKey(newOwmKey.trim());
        }

        preferenceService.setUnitSystem(unitSelect.getValue());

        Notification notif = Notification.show("Settings updated", 2500, Notification.Position.BOTTOM_END);
        notif.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

        close();
        if (onSettingsSaved != null) {
            onSettingsSaved.run();
        }
    }

    /**
     * Safely mask key to display only the last 4 characters as confirmation, e.g. "...40jz"
     */
    private String maskKey(String key) {
        if (key == null || key.length() < 6) {
            return "••••";
        }
        return "..." + key.substring(key.length() - 4);
    }
}
