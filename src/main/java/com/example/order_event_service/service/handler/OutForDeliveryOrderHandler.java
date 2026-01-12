package com.example.order_event_service.service.handler;

import com.example.order_event_service.domain.OrderStatus;
import com.example.order_event_service.entity.OrderEvent;
import com.example.order_event_service.repository.OrderEventRepository;
import com.example.order_event_service.util.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OutForDeliveryOrderHandler {

    private static final Logger log =
            LoggerFactory.getLogger(OutForDeliveryOrderHandler.class);

    private final OrderEventRepository repository;
    private final Validator validator;

    public OutForDeliveryOrderHandler(
            OrderEventRepository repository,
            Validator validator
    ) {
        this.repository = repository;
        this.validator = validator;
    }

    public Optional<OrderEvent> handle(String shipmentNumber) {

        OrderEvent lastEvent = validator.validateOrderExists(
                shipmentNumber,
                repository.findTopByShipmentNumberOrderByReceivedAtDesc(shipmentNumber)
        );

        int previousStatus = lastEvent.getStatusCode();

        // sprawdzam ostatni event; brak INSERT jeśli status już ustawiony
        validator.validateCanBeOutForDelivery(
                shipmentNumber,
                previousStatus
        );

        OrderEvent newEvent = new OrderEvent();
        newEvent.setShipmentNumber(lastEvent.getShipmentNumber());
        newEvent.setRecipientEmail(lastEvent.getRecipientEmail());
        newEvent.setRecipientCountryCode(lastEvent.getRecipientCountryCode());
        newEvent.setSenderCountryCode(lastEvent.getSenderCountryCode());
        newEvent.setStatusCode(OrderStatus.OUT_FOR_DELIVERY.getCode());

        repository.save(newEvent);

        log.info(
                "Order status appended successfully [shipmentNumber={}, oldStatusCode={}, newStatusCode={}, enum={}]",
                shipmentNumber,
                previousStatus,
                newEvent.getStatusCode(),
                OrderStatus.OUT_FOR_DELIVERY
        );

        return Optional.of(newEvent);
    }
}
