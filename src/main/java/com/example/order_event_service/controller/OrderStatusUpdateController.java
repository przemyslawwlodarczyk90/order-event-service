package com.example.order_event_service.controller;

import com.example.order_event_service.domain.OrderStatus;
import com.example.order_event_service.dto.OrderStatusUpdateDto;

import com.example.order_event_service.kafka.OrderEventKafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderStatusUpdateController {

    private static final Logger log =
            LoggerFactory.getLogger(OrderStatusUpdateController.class);

    private final OrderEventKafkaProducer producer;

    public OrderStatusUpdateController(OrderEventKafkaProducer producer) {
        this.producer = producer;
    }

    @PatchMapping("/{shipmentNumber}/status/packed-and-shipped")
    public ResponseEntity<Void> markOrderAsPackedAndShipped(
            @PathVariable String shipmentNumber
    ) {
        log.info(
                "Received STATUS UPDATE request [shipmentNumber={}, status={}]",
                shipmentNumber,
                OrderStatus.PACKED_AND_SHIPPED
        );

        OrderStatusUpdateDto event = new OrderStatusUpdateDto();
        event.setShipmentNumber(shipmentNumber);
        event.setStatus(OrderStatus.PACKED_AND_SHIPPED);

        producer.sendOrderEvent(shipmentNumber, event);

        return ResponseEntity.accepted().build(); // 🔥 ASYNC
    }

    @PatchMapping("/{shipmentNumber}/status/out-for-delivery")
    public ResponseEntity<Void> markOrderAsOutForDelivery(
            @PathVariable String shipmentNumber
    ) {
        log.info(
                "Received STATUS UPDATE request [shipmentNumber={}, status={}]",
                shipmentNumber,
                OrderStatus.OUT_FOR_DELIVERY
        );

        OrderStatusUpdateDto event = new OrderStatusUpdateDto();
        event.setShipmentNumber(shipmentNumber);
        event.setStatus(OrderStatus.OUT_FOR_DELIVERY);

        producer.sendOrderEvent(shipmentNumber, event);

        return ResponseEntity.accepted().build();
    }
}