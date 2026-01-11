package com.example.order_event_service.controller;

import com.example.order_event_service.dto.OrderRequestDto;
import com.example.order_event_service.service.OrderEventFacade;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderReceiverController {

    private static final Logger log =
            LoggerFactory.getLogger(OrderReceiverController.class);

    private final OrderEventFacade service;

    public OrderReceiverController(OrderEventFacade service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Void> receiveOrderEvent(
            @Valid @RequestBody OrderRequestDto request
    ) {
        log.info(
                "Received order event request [shipmentNumber={}, statusCode={}]",
                request.getShipmentNumber(),
                request.getStatusCode()
        );

        service.processOrderEvent(request);

        return ResponseEntity.accepted().build();
    }
}
