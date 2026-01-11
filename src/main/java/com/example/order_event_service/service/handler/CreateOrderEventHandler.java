package com.example.order_event_service.service.handler;

import com.example.order_event_service.dto.OrderRequestDto;
import com.example.order_event_service.entity.OrderEvent;
import com.example.order_event_service.mapper.OrderEventMapper;
import com.example.order_event_service.repository.OrderEventRepository;
import com.example.order_event_service.util.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CreateOrderEventHandler {

    private static final Logger log =
            LoggerFactory.getLogger(CreateOrderEventHandler.class);

    private final OrderEventRepository repository;
    private final OrderEventMapper mapper;
    private final Validator validator;

    public CreateOrderEventHandler(
            OrderEventRepository repository,
            OrderEventMapper mapper,
            Validator validator
    ) {
        this.repository = repository;
        this.mapper = mapper;
        this.validator = validator;
    }

    public OrderEvent handle(OrderRequestDto request) {

        String shipmentNumber = request.getShipmentNumber();
        int statusCode = request.getStatusCode();

        log.info(
                "Processing order event [shipmentNumber={}, statusCode={}]",
                shipmentNumber,
                statusCode
        );

        validator.validateOrderDoesNotExist(
                shipmentNumber,
                repository.findByShipmentNumber(shipmentNumber)
        );

        validator.validateInitialStatusAllowed(shipmentNumber, statusCode);

        OrderEvent event = mapper.toEntity(request);
        repository.save(event);

        log.info(
                "Order event processed successfully [shipmentNumber={}]",
                shipmentNumber
        );

        return event;
    }
}
