package com.example.order_event_service.service.handler;

import com.example.order_event_service.domain.OrderStatus;
import com.example.order_event_service.entity.OrderEvent;
import com.example.order_event_service.repository.OrderEventRepository;
import com.example.order_event_service.util.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

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

    public OrderEvent handle(String shipmentNumber) {

        OrderEvent event = validator.validateOrderExists(
                shipmentNumber,
                repository.findByShipmentNumber(shipmentNumber)
        );

        int previousStatus = event.getStatusCode();

        validator.validateCanBeOutForDelivery(shipmentNumber, previousStatus);

        event.setStatusCode(OrderStatus.OUT_FOR_DELIVERY.getCode());
        repository.save(event);

        log.info(
                "Order status updated successfully [shipmentNumber={}, oldStatusCode={}, newStatusCode={}, enum={}]",
                shipmentNumber,
                previousStatus,
                event.getStatusCode(),
                OrderStatus.OUT_FOR_DELIVERY
        );

        return event;
    }
}

