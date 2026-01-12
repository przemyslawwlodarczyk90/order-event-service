package com.example.order_event_service.service.handler;

import com.example.order_event_service.entity.OrderEvent;
import com.example.order_event_service.repository.OrderEventRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class GetAllOrderEventsHandler {

    private final OrderEventRepository repository;

    public GetAllOrderEventsHandler(OrderEventRepository repository) {
        this.repository = repository;
    }

    public List<OrderEvent> handle() {
        return repository.findAll();
    }
}
