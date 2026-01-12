package com.example.order_event_service.controller;

import com.example.order_event_service.entity.OrderEvent;
import com.example.order_event_service.service.OrderEventFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderEventQueryController {

    private final OrderEventFacade facade;

    public OrderEventQueryController(OrderEventFacade facade) {
        this.facade = facade;
    }

    /**
     * Historia eventów dla jednego zamówienia
     */
    @GetMapping("/{shipmentNumber}/events")
    public ResponseEntity<List<OrderEvent>> getOrderEvents(
            @PathVariable String shipmentNumber
    ) {
        return ResponseEntity.ok(
                facade.getOrderEvents(shipmentNumber)
        );
    }

    /**
     * Wszystkie eventy w systemie (audit globalny)
     */
    @GetMapping("/events")
    public ResponseEntity<List<OrderEvent>> getAllOrderEvents() {
        return ResponseEntity.ok(
                facade.getAllOrderEvents()
        );
    }
}
