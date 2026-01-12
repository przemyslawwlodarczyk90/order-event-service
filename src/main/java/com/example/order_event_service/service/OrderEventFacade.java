package com.example.order_event_service.service;

import com.example.order_event_service.dto.OrderRequestDto;
import com.example.order_event_service.dto.OrderStatusUpdateDto;
import com.example.order_event_service.entity.OrderEvent;
import com.example.order_event_service.notification.EmailNotificationFacade;
import com.example.order_event_service.service.handler.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderEventFacade {

    private final CreateOrderEventHandler createHandler;
    private final PackAndShipOrderHandler packHandler;
    private final OutForDeliveryOrderHandler outForDeliveryHandler;
    private final EmailNotificationFacade notificationFacade;
    private final GetOrderEventsHandler getOrderEventsHandler;
    private final GetAllOrderEventsHandler getAllOrderEventsHandler;

    public OrderEventFacade(
            CreateOrderEventHandler createHandler,
            PackAndShipOrderHandler packHandler,
            OutForDeliveryOrderHandler outForDeliveryHandler,
            EmailNotificationFacade notificationFacade,
            GetOrderEventsHandler getOrderEventsHandler,
            GetAllOrderEventsHandler getAllOrderEventsHandler
    ) {
        this.createHandler = createHandler;
        this.packHandler = packHandler;
        this.outForDeliveryHandler = outForDeliveryHandler;
        this.notificationFacade = notificationFacade;
        this.getOrderEventsHandler = getOrderEventsHandler;
        this.getAllOrderEventsHandler = getAllOrderEventsHandler;
    }

    public void processOrderEvent(OrderRequestDto request) {
        OrderEvent event = createHandler.handle(request);
        notificationFacade.sendOrderAccepted(event);
    }

    @Transactional
    public void markOrderAsPackedAndShipped(String shipmentNumber) {
        Optional<OrderEvent> event = packHandler.handle(shipmentNumber);

        event.ifPresent(notificationFacade::sendPackedAndShipped);
    }

    @Transactional
    public void markOrderAsOutForDelivery(String shipmentNumber) {
        Optional<OrderEvent> event = outForDeliveryHandler.handle(shipmentNumber);

        event.ifPresent(notificationFacade::sendOutForDelivery);
    }

    public void processStatusUpdate(OrderStatusUpdateDto event) {

        switch (event.getStatus()) {
            case PACKED_AND_SHIPPED ->
                    markOrderAsPackedAndShipped(event.getShipmentNumber());

            case OUT_FOR_DELIVERY ->
                    markOrderAsOutForDelivery(event.getShipmentNumber());

            default ->
                    throw new IllegalStateException(
                            "Unsupported status: " + event.getStatus()
                    );
        }
    }


    public List<OrderEvent> getOrderEvents(String shipmentNumber) {
        return getOrderEventsHandler.handle(shipmentNumber);
    }

    public List<OrderEvent> getAllOrderEvents() {
        return getAllOrderEventsHandler.handle();
    }
}
