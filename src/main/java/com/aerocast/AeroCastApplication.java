package com.aerocast;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * AeroCast Weather Information Dashboard
 * Production-ready, pure-Java full-stack application built with Spring Boot 3 and Vaadin Flow.
 */
@SpringBootApplication
@Theme(variant = Lumo.DARK)
@PWA(name = "AeroCast Weather Intelligence", shortName = "AeroCast", offline = false)
public class AeroCastApplication implements AppShellConfigurator {

    public static void main(String[] args) {
        loadDotEnv();
        SpringApplication.run(AeroCastApplication.class, args);
    }

    /**
     * Automatically reads .env file key-value pairs into system properties on boot.
     */
    private static void loadDotEnv() {
        Path envPath = Path.of(".env");
        if (!Files.exists(envPath)) {
            return;
        }
        try {
            List<String> lines = Files.readAllLines(envPath);
            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                int idx = line.indexOf('=');
                if (idx > 0) {
                    String key = line.substring(0, idx).trim();
                    String val = line.substring(idx + 1).trim();
                    if ((val.startsWith("\"") && val.endsWith("\"")) || (val.startsWith("'") && val.endsWith("'"))) {
                        val = val.substring(1, val.length() - 1);
                    }
                    if (!key.isEmpty() && !val.isEmpty()) {
                        System.setProperty(key, val);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Note: Could not parse .env file: " + e.getMessage());
        }
    }
}
