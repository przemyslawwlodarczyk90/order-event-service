package com.example.order_event_service.settings;

import com.example.order_event_service.repository.SystemSettingsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SettingsInitializer implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(SettingsInitializer.class);
    private final SystemSettingsRepository repository;

    public SettingsInitializer(SystemSettingsRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (!repository.existsById(1L)) {
            log.info("Initializing system settings with default values (ID=1)...");
            SystemSettings settings = new SystemSettings();
            repository.save(settings);
            log.info("System settings initialized.");
        } else {
            log.info("System settings already present in database.");
        }
    }
}