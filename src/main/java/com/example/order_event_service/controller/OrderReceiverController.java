package com.example.order_event_service.controller;

import com.example.order_event_service.dto.OrderRequestDto;
import com.example.order_event_service.kafka.OrderEventKafkaProducer;
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

    private final OrderEventKafkaProducer producer;

    public OrderReceiverController(OrderEventKafkaProducer producer) {
        this.producer = producer;
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

        producer.sendOrderEvent(
                request.getShipmentNumber(),
                request
        );

        return ResponseEntity.accepted().build();
    }
}
