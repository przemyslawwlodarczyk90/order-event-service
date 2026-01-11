package com.example.order_event_service.notification;

import com.example.order_event_service.entity.OrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationService {

    private static final Logger log =
            LoggerFactory.getLogger(EmailNotificationService.class);

    public void sendOrderEventNotification(OrderEvent event) {
        log.info(
                "MOCK EMAIL SENT [shipmentNumber={}, recipientEmail={}, statusCode={}]",
                event.getShipmentNumber(),
                event.getRecipientEmail(),
                event.getStatusCode()
        );
    }
}
