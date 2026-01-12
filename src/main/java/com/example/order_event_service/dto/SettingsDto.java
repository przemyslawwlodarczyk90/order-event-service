package com.example.order_event_service.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SettingsDto {
    private int consumerConcurrency;
    private boolean emailEnabled;
    private int emailRatePerSecond;
}