package com.example.order_event_service.mapper;

import com.example.order_event_service.dto.OrderRequestDto;
import com.example.order_event_service.entity.OrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class OrderEventMapper {

    private static final Logger log =
            LoggerFactory.getLogger(OrderEventMapper.class);

    public OrderEvent toEntity(OrderRequestDto request) {
        log.info(
                "Mapping OrderRequestDto to OrderEvent [shipmentNumber={}]",
                request.getShipmentNumber()
        );

        OrderEvent event = new OrderEvent();
        event.setShipmentNumber(request.getShipmentNumber());
        event.setRecipientEmail(request.getRecipientEmail());
        event.setRecipientCountryCode(request.getRecipientCountryCode());
        event.setSenderCountryCode(request.getSenderCountryCode());
        event.setStatusCode(request.getStatusCode());

        return event;
    }
}
