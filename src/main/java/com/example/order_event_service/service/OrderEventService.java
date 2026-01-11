package com.example.order_event_service.service;

import com.example.order_event_service.dto.OrderRequestDto;
import com.example.order_event_service.entity.OrderEvent;
import com.example.order_event_service.mapper.OrderEventMapper;
import com.example.order_event_service.repository.OrderEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OrderEventService {

    private static final Logger log =
            LoggerFactory.getLogger(OrderEventService.class);

    private final OrderEventRepository repository;
    private final OrderEventMapper mapper;

    public OrderEventService(OrderEventRepository repository,
                             OrderEventMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public void processOrderEvent(OrderRequestDto request) {
        log.info(
                "Processing order event [shipmentNumber={}]",
                request.getShipmentNumber()
        );

        OrderEvent event = mapper.toEntity(request);
        repository.save(event);

        log.info(
                "Order event persisted [shipmentNumber={}]",
                event.getShipmentNumber()
        );
    }
}
