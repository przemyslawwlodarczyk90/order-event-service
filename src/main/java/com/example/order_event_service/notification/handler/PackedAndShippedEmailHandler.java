package com.example.order_event_service.notification.handler;

import com.example.order_event_service.entity.OrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class PackedAndShippedEmailHandler implements EmailNotificationHandler {

    private static final Logger log =
            LoggerFactory.getLogger(PackedAndShippedEmailHandler.class);

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void send(OrderEvent event) {

        log.info(
                "Order packed and shipped notification sent [email={}, shipmentNumber={}]",
                event.getRecipientEmail(),
                event.getShipmentNumber()
        );

        log.info(
                "Order status change details [shipmentNumber={}, newStatusCode={}]",
                event.getShipmentNumber(),
                event.getStatusCode()
        );

        log.info(
                "Order status updated at [{}]",
                event.getReceivedAt()
                        .atZone(ZoneId.systemDefault())
                        .format(DATE_TIME_FORMATTER)
        );

        log.info(
                """
                Email content (MOCK):
                ----------------------------------------
                To: {}

                Dear Customer,

                Your order with number {} has been packed and shipped.

                Sender country code: {}
                Recipient country code: {}
                Current order status: {}

                You will be informed once the package is out for delivery.

                Thank you for choosing our service.
                ----------------------------------------
                """,
                event.getRecipientEmail(),
                event.getShipmentNumber(),
                event.getSenderCountryCode(),
                event.getRecipientCountryCode(),
                event.getStatusCode()
        );
    }
}
