package com.example.order_event_service.dto;

import com.example.order_event_service.domain.OrderStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderStatusUpdateDto {

    private String shipmentNumber;
    private OrderStatus status;

}
