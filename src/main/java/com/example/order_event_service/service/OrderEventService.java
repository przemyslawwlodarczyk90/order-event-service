package com.example.order_event_service.service;

import com.example.order_event_service.domain.OrderStatus;
import com.example.order_event_service.dto.OrderRequestDto;
import com.example.order_event_service.entity.OrderEvent;
import com.example.order_event_service.mapper.OrderEventMapper;
import com.example.order_event_service.notification.EmailNotificationService;
import com.example.order_event_service.repository.OrderEventRepository;
import com.example.order_event_service.util.Validator;
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
    private final Validator validator;

    public OrderEventService(OrderEventRepository repository,
                             OrderEventMapper mapper,
                             EmailNotificationService emailNotificationService,
                             Validator validator) {
        this.repository = repository;
        this.mapper = mapper;
        this.emailNotificationService = emailNotificationService;
        this.validator = validator;
    }

    public void processOrderEvent(OrderRequestDto request) {

        String shipmentNumber = request.getShipmentNumber();
        int statusCode = request.getStatusCode();

        log.info(
                "Processing order event [shipmentNumber={}, statusCode={}]",
                shipmentNumber,
                statusCode
        );

        // 1️⃣ Walidacje
        validator.validateOrderDoesNotExist(
                shipmentNumber,
                repository.findByShipmentNumber(shipmentNumber)
        );

        validator.validateInitialStatusAllowed(shipmentNumber, statusCode);

        // 2️⃣ Normalne przetwarzanie
        OrderEvent event = mapper.toEntity(request);
        repository.save(event);

        emailNotificationService.sendOrderAcceptanceConfirmation(event);

        log.info(
                "Order event processed successfully [shipmentNumber={}]",
                shipmentNumber
        );
    }

    @Transactional
    public void markOrderAsPackedAndShipped(String shipmentNumber) {

        OrderEvent event = validator.validateOrderExists(
                shipmentNumber,
                repository.findByShipmentNumber(shipmentNumber)
        );

        int previousStatus = event.getStatusCode();

        // 1️⃣ Walidacja
        if (validator.validateAlreadyPackedAndShipped(
                shipmentNumber,
                previousStatus
        )) {
            return;
        }

        // 2️⃣ Przetwarzanie
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

    @Transactional
    public void markOrderAsOutForDelivery(String shipmentNumber) {

        OrderEvent event = validator.validateOrderExists(
                shipmentNumber,
                repository.findByShipmentNumber(shipmentNumber)
        );

        int previousStatus = event.getStatusCode();

        // 1️⃣ Walidacja
        validator.validateCanBeOutForDelivery(shipmentNumber, previousStatus);

        // 2️⃣ Przetwarzanie
        event.setStatusCode(OrderStatus.OUT_FOR_DELIVERY.getCode());
        repository.save(event);

        emailNotificationService.sendOrderOutForDeliveryNotification(event);

        log.info(
                "Order status updated successfully [shipmentNumber={}, oldStatusCode={}, newStatusCode={}, enum={}]",
                shipmentNumber,
                previousStatus,
                event.getStatusCode(),
                OrderStatus.OUT_FOR_DELIVERY
        );
    }
}
