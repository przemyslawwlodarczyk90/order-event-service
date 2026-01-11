package com.example.order_event_service.notification;

import com.example.order_event_service.entity.OrderEvent;
import com.example.order_event_service.notification.handler.OrderAcceptedEmailHandler;
import com.example.order_event_service.notification.handler.PackedAndShippedEmailHandler;
import com.example.order_event_service.notification.handler.OutForDeliveryEmailHandler;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationFacade {

    private final OrderAcceptedEmailHandler acceptedHandler;
    private final PackedAndShippedEmailHandler packedHandler;
    private final OutForDeliveryEmailHandler outHandler;

    public EmailNotificationFacade(
            OrderAcceptedEmailHandler acceptedHandler,
            PackedAndShippedEmailHandler packedHandler,
            OutForDeliveryEmailHandler outHandler
    ) {
        this.acceptedHandler = acceptedHandler;
        this.packedHandler = packedHandler;
        this.outHandler = outHandler;
    }

    public void sendOrderAccepted(OrderEvent event) {
        acceptedHandler.send(event);
    }

    public void sendPackedAndShipped(OrderEvent event) {
        packedHandler.send(event);
    }

    public void sendOutForDelivery(OrderEvent event) {
        outHandler.send(event);
    }
}
