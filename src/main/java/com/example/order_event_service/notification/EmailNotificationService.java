package com.example.order_event_service.notification;

import com.example.order_event_service.entity.OrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class EmailNotificationService {

    private static final Logger log =
            LoggerFactory.getLogger(EmailNotificationService.class);

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Mock confirmation email sent to order recipient.
     * Simulates business notification without real email delivery.
     */
    public void sendOrderAcceptanceConfirmation(OrderEvent event) {

        // LOG 1 – informacja biznesowa + adresat
        log.info(
                "Order acceptance notification sent to recipient [email={}, shipmentNumber={}]",
                event.getRecipientEmail(),
                event.getShipmentNumber()
        );

        // LOG 2 – identyfikacja zamówienia
        log.info(
                "Order details [shipmentNumber={}, statusCode={}]",
                event.getShipmentNumber(),
                event.getStatusCode()
        );

        // LOG 3 – audyt czasowy
        log.info(
                "Order registered at [{}]",
                event.getReceivedAt()
                        .atZone(ZoneId.systemDefault())
                        .format(DATE_TIME_FORMATTER)
        );

        // LOG 4 – treść mock maila (pełny kontekst)
        log.info(
                """
                Email content (MOCK):
                ----------------------------------------
                To: {}

                Dear Customer,

                Your order with number {} has been successfully registered.

                Sender country code: {}
                Recipient country code: {}
                Current order status: {}

                Thank you for your order.
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
