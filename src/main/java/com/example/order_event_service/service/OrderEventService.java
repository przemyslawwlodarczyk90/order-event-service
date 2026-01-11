package com.example.order_event_service.service;

import com.example.order_event_service.domain.OrderStatus;
import com.example.order_event_service.dto.OrderRequestDto;
import com.example.order_event_service.entity.OrderEvent;
import com.example.order_event_service.mapper.OrderEventMapper;
import com.example.order_event_service.notification.EmailNotificationService;
import com.example.order_event_service.repository.OrderEventRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OrderEventService {

    private static final Logger log =
            LoggerFactory.getLogger(OrderEventService.class);

    private final OrderEventRepository repository;
    private final OrderEventMapper mapper;
    private final EmailNotificationService emailNotificationService;

    public OrderEventService(OrderEventRepository repository,
                             OrderEventMapper mapper,
                             EmailNotificationService emailNotificationService) {
        this.repository = repository;
        this.mapper = mapper;
        this.emailNotificationService = emailNotificationService;
    }

    public void processOrderEvent(OrderRequestDto request) {
        log.info(
                "Processing order event [shipmentNumber={}]",
                request.getShipmentNumber()
        );

        OrderEvent event = mapper.toEntity(request);
        repository.save(event);

        emailNotificationService.sendOrderAcceptanceConfirmation(event);

        log.info(
                "Order event processed successfully [shipmentNumber={}]",
                event.getShipmentNumber()
        );
    }


    @Transactional
    public void markOrderAsPackedAndShipped(String shipmentNumber) {

        log.info(
                "Processing order status change [shipmentNumber={}, newStatus={}, code={}]",
                shipmentNumber,
                OrderStatus.PACKED_AND_SHIPPED,
                OrderStatus.PACKED_AND_SHIPPED.getCode()
        );

        OrderEvent event = repository.findByShipmentNumber(shipmentNumber)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Order not found for shipmentNumber=" + shipmentNumber
                        )
                );

        int previousStatus = event.getStatusCode();

        if (previousStatus == OrderStatus.PACKED_AND_SHIPPED.getCode()) {
            log.info(
                    "Order already in target status [shipmentNumber={}, status={}, code={}]",
                    shipmentNumber,
                    OrderStatus.PACKED_AND_SHIPPED,
                    previousStatus
            );
            return;
        }


        event.setStatusCode(OrderStatus.PACKED_AND_SHIPPED.getCode());
        repository.save(event);

        emailNotificationService.sendOrderPackedAndShippedNotification(event);

        log.info(
                "Order status updated successfully [shipmentNumber={}, oldStatusCode={}, newStatusCode={}, enum={}]",
                shipmentNumber,
                previousStatus,
                event.getStatusCode(),
                OrderStatus.PACKED_AND_SHIPPED
        );
    }
}
