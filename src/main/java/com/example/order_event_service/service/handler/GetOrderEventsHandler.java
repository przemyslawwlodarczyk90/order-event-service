package com.example.order_event_service.service.handler;

import com.example.order_event_service.entity.OrderEvent;
import com.example.order_event_service.repository.OrderEventRepository;
import com.example.order_event_service.util.Validator;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetOrderEventsHandler {

    private final OrderEventRepository repository;
    private final Validator validator;

    public GetOrderEventsHandler(
            OrderEventRepository repository,
            Validator validator
    ) {
        this.repository = repository;
        this.validator = validator;
    }

    public List<OrderEvent> handle(String shipmentNumber) {

        validator.validateOrderExists(
                shipmentNumber,
                repository.findTopByShipmentNumberOrderByReceivedAtDesc(shipmentNumber)
        );

        return repository.findAllByShipmentNumberOrderByReceivedAtAsc(
                shipmentNumber
        );
    }
}
