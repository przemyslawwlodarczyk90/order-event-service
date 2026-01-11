package com.example.order_event_service.service;

import com.example.order_event_service.domain.OrderStatus;
import com.example.order_event_service.dto.OrderRequestDto;
import com.example.order_event_service.entity.OrderEvent;
import com.example.order_event_service.exception.InvalidOrderStatusTransitionException;
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

        String shipmentNumber = request.getShipmentNumber();
        int statusCode = request.getStatusCode();

        log.info(
                "Processing order event [shipmentNumber={}, statusCode={}]",
                shipmentNumber,
                statusCode
        );

        // 1️⃣ Czy event już istnieje?
        repository.findByShipmentNumber(shipmentNumber)
                .ifPresent(existing -> {
                    log.warn(
                            "Duplicate order event blocked [shipmentNumber={}, existingStatusCode={}]",
                            shipmentNumber,
                            existing.getStatusCode()
                    );
                    throw new InvalidOrderStatusTransitionException(
                            "Order event already exists for shipmentNumber=" + shipmentNumber
                    );
                });

        // 2️⃣ Walidacja niedozwolonych statusów początkowych
        if (statusCode == OrderStatus.PACKED_AND_SHIPPED.getCode()
                || statusCode == OrderStatus.OUT_FOR_DELIVERY.getCode()) {

            log.warn(
                    "Invalid initial order status blocked [shipmentNumber={}, statusCode={}]",
                    shipmentNumber,
                    statusCode
            );

            throw new InvalidOrderStatusTransitionException(
                    "Initial order event cannot have shipment-related status"
            );
        }

        // 3️⃣ Normalne przetwarzanie
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

        OrderEvent event = repository.findByShipmentNumber(shipmentNumber)
                .orElseThrow(() ->
                        new InvalidOrderStatusTransitionException(
                                "Order not found for shipmentNumber=" + shipmentNumber
                        )
                );

        int previousStatus = event.getStatusCode();

        // 1️⃣ Czy status już jest docelowy?
        if (previousStatus == OrderStatus.PACKED_AND_SHIPPED.getCode()) {
            log.info(
                    "Order already in target status [shipmentNumber={}, statusCode={}]",
                    shipmentNumber,
                    previousStatus
            );
            return;
        }

        // 2️⃣ Brak dodatkowych ograniczeń – przejście dozwolone
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

        OrderEvent event = repository.findByShipmentNumber(shipmentNumber)
                .orElseThrow(() ->
                        new InvalidOrderStatusTransitionException(
                                "Order not found for shipmentNumber=" + shipmentNumber
                        )
                );

        int previousStatus = event.getStatusCode();

        // 1️⃣ Czy zamówienie jest w wymaganym stanie poprzednim?
        if (previousStatus != OrderStatus.PACKED_AND_SHIPPED.getCode()) {
            log.warn(
                    "Invalid order status transition blocked [shipmentNumber={}, currentStatusCode={}, requiredStatusCode={}]",
                    shipmentNumber,
                    previousStatus,
                    OrderStatus.PACKED_AND_SHIPPED.getCode()
            );
            throw new InvalidOrderStatusTransitionException(
                    "Order must be PACKED_AND_SHIPPED before OUT_FOR_DELIVERY"
            );
        }

        // 2️⃣ Przejście dozwolone – zmiana statusu
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
    }}