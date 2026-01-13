package com.example.order_event_service.notification.handler;

import com.example.order_event_service.entity.OrderEvent;

public interface EmailNotificationHandler {

    void send(OrderEvent event);
}
