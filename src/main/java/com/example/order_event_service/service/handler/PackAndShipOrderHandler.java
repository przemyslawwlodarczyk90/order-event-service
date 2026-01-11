package com.example.order_event_service.service.handler;

import com.example.order_event_service.domain.OrderStatus;
import com.example.order_event_service.entity.OrderEvent;
import com.example.order_event_service.repository.OrderEventRepository;
import com.example.order_event_service.util.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

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

    public OrderEvent handle(String shipmentNumber) {

        OrderEvent event = validator.validateOrderExists(
                shipmentNumber,
                repository.findByShipmentNumber(shipmentNumber)
        );

        int previousStatus = event.getStatusCode();

        if (validator.validateAlreadyPackedAndShipped(
                shipmentNumber,
                previousStatus
        )) {
            return event;
        }

        event.setStatusCode(OrderStatus.PACKED_AND_SHIPPED.getCode());
        repository.save(event);

        log.info(
                "Order status updated successfully [shipmentNumber={}, oldStatusCode={}, newStatusCode={}, enum={}]",
                shipmentNumber,
                previousStatus,
                event.getStatusCode(),
                OrderStatus.PACKED_AND_SHIPPED
        );

        return event;
    }
}
