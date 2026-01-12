package com.example.order_event_service.settings;

import com.example.order_event_service.dto.SettingsDto;
import com.example.order_event_service.repository.SystemSettingsRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class SystemSettingsService {
    private static final Logger log = LoggerFactory.getLogger(SystemSettingsService.class);
    private final SystemSettingsRepository repository;

    public SystemSettingsService(SystemSettingsRepository repository) {
        this.repository = repository;
    }

    public SystemSettings get() {
        return repository.findById(1L)
                .orElseThrow(() -> new RuntimeException("System settings not initialized!"));
    }

    public void update(SettingsDto dto) {
        log.info("Attempting to update system settings with Pessimistic Lock...");

        SystemSettings settings = repository.findAndLockById1()
                .orElseThrow(() -> new RuntimeException("Settings not found"));

        settings.setConsumerConcurrency(dto.getConsumerConcurrency());
        settings.setEmailEnabled(dto.isEmailEnabled());
        settings.setEmailRatePerSecond(dto.getEmailRatePerSecond());

        log.info("System settings updated successfully [Concurrency: {}, Emails: {}]",
                settings.getConsumerConcurrency(), settings.isEmailEnabled());
    }
}