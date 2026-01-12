package com.example.order_event_service.settings;

import com.example.order_event_service.repository.SystemSettingsRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * SettingsCache przechowuje aktualne ustawienia systemowe w pamięci aplikacji.
 *
 * Działa jako warstwa cache pomiędzy bazą danych a komponentami
 * intensywnie korzystającymi z konfiguracji (np. Kafka Consumer).
 *
 * - Przy starcie aplikacji pobiera ustawienia z bazy danych.
 * - Co kilka sekund odświeża je w tle (@Scheduled).
 * - Komponenty runtime odczytują dane z pamięci, a nie z bazy.
 *
 * Dzięki temu:
 * - baza danych nie jest odpytywana przy każdym evencie,
 * - zmiany konfiguracyjne wchodzą w życie niemal natychmiast,
 * - system zachowuje wysoką wydajność przy dużym wolumenie zdarzeń.
 */

@Component
public class SettingsCache {
    private static final Logger log = LoggerFactory.getLogger(SettingsCache.class);
    private final SystemSettingsRepository repository;
    private volatile SystemSettings cachedSettings;

    public SettingsCache(SystemSettingsRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void init() {
        refresh(); // Pierwsze pobranie przy starcie
    }

    @Scheduled(fixedRate = 5000)
    public void refresh() {
        repository.findById(1L).ifPresent(s -> {
            this.cachedSettings = s;
            log.debug("Settings cache refreshed from DB.");
        });
    }

    public SystemSettings get() {
        return cachedSettings;
    }
}