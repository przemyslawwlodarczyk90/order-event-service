package com.example.order_event_service.controller;

import com.example.order_event_service.dto.SettingsDto;
import com.example.order_event_service.settings.SystemSettings;
import com.example.order_event_service.settings.SystemSettingsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/settings")
public class AdminSettingsController {
    private static final Logger log = LoggerFactory.getLogger(AdminSettingsController.class);
    private final SystemSettingsService service;

    public AdminSettingsController(SystemSettingsService service) {
        this.service = service;
    }

    @GetMapping
    public SystemSettings getSettings() {
        return service.get();
    }

    @PutMapping
    public void updateSettings(@RequestBody SettingsDto dto) {
        log.info("REST API: Received request to update system settings. Payload: {}", dto);
        service.update(dto);
        log.info("REST API: Settings update process finished successfully.");
    }
}