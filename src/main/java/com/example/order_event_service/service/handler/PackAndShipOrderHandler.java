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
public class PackAndShipOrderHandler {

    private static final Logger log =
            LoggerFactory.getLogger(PackAndShipOrderHandler.class);

    private final OrderEventRepository repository;
    private final Validator validator;

    public PackAndShipOrderHandler(
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
        if (validator.validateAlreadyPackedAndShipped(
                shipmentNumber,
                previousStatus
        )) {
            return Optional.empty();
        }

        OrderEvent newEvent = new OrderEvent();
        newEvent.setShipmentNumber(lastEvent.getShipmentNumber());
        newEvent.setRecipientEmail(lastEvent.getRecipientEmail());
        newEvent.setRecipientCountryCode(lastEvent.getRecipientCountryCode());
        newEvent.setSenderCountryCode(lastEvent.getSenderCountryCode());
        newEvent.setStatusCode(OrderStatus.PACKED_AND_SHIPPED.getCode());

        repository.save(newEvent);

        log.info(
                "Order status appended successfully [shipmentNumber={}, oldStatusCode={}, newStatusCode={}, enum={}]",
                shipmentNumber,
                previousStatus,
                newEvent.getStatusCode(),
                OrderStatus.PACKED_AND_SHIPPED
        );

        return Optional.of(newEvent);
    }
}
