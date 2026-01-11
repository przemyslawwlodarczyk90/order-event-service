package com.example.order_event_service.service;

import com.example.order_event_service.dto.OrderRequestDto;
import com.example.order_event_service.entity.OrderEvent;
import com.example.order_event_service.notification.EmailNotificationFacade;
import com.example.order_event_service.service.handler.CreateOrderEventHandler;
import com.example.order_event_service.service.handler.OutForDeliveryOrderHandler;
import com.example.order_event_service.service.handler.PackAndShipOrderHandler;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class OrderEventFacade {

    private final CreateOrderEventHandler createHandler;
    private final PackAndShipOrderHandler packHandler;
    private final OutForDeliveryOrderHandler outForDeliveryHandler;
    private final EmailNotificationFacade notificationFacade;

    public OrderEventFacade(
            CreateOrderEventHandler createHandler,
            PackAndShipOrderHandler packHandler,
            OutForDeliveryOrderHandler outForDeliveryHandler,
            EmailNotificationFacade notificationFacade
    ) {
        this.createHandler = createHandler;
        this.packHandler = packHandler;
        this.outForDeliveryHandler = outForDeliveryHandler;
        this.notificationFacade = notificationFacade;
    }

    public void processOrderEvent(OrderRequestDto request) {
        OrderEvent event = createHandler.handle(request);
        notificationFacade.sendOrderAccepted(event);
    }

    @Transactional
    public void markOrderAsPackedAndShipped(String shipmentNumber) {
        OrderEvent event = packHandler.handle(shipmentNumber);
        notificationFacade.sendPackedAndShipped(event);
    }

    @Transactional
    public void markOrderAsOutForDelivery(String shipmentNumber) {
        OrderEvent event = outForDeliveryHandler.handle(shipmentNumber);
        notificationFacade.sendOutForDelivery(event);
    }
}
