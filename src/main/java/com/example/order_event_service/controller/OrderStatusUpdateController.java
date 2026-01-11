package com.example.order_event_service.controller;

import com.example.order_event_service.domain.OrderStatus;
import com.example.order_event_service.service.OrderEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderStatusUpdateController {

    private static final Logger log =
            LoggerFactory.getLogger(OrderStatusUpdateController.class);

    private final OrderEventService service;

    public OrderStatusUpdateController(OrderEventService service) {
        this.service = service;
    }

    @PatchMapping("/{shipmentNumber}/status/packed-and-shipped")
    public ResponseEntity<Void> markOrderAsPackedAndShipped(
            @PathVariable String shipmentNumber
    ) {
        log.info(
                "Received order status update request [shipmentNumber={}, status={}]",
                shipmentNumber,
                OrderStatus.PACKED_AND_SHIPPED
        );

        service.markOrderAsPackedAndShipped(shipmentNumber);

        return ResponseEntity.accepted().build();
    }
}
