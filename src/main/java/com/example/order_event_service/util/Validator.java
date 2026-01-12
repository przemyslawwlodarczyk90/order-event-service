package com.example.order_event_service.util;

import com.example.order_event_service.domain.OrderStatus;
import com.example.order_event_service.entity.OrderEvent;
import com.example.order_event_service.exception.InvalidOrderStatusTransitionException;
import com.example.order_event_service.exception.OrderNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class Validator {

    private static final Logger log =
            LoggerFactory.getLogger(Validator.class);

    /**
     * Walidacja: event nie może już istnieć
     *
     * Uwaga: w modelu append-only audit ta walidacja docelowo znika,
     * ale zostawiamy ją na ten moment, jeśli jeszcze używasz jej w POST.
     */
    public void validateOrderDoesNotExist(String shipmentNumber,
                                          Optional<OrderEvent> existingEvent) {

        existingEvent.ifPresent(existing -> {
            log.warn(
                    "validator - duplicate order event blocked [shipmentNumber={}, existingStatusCode={}]",
                    shipmentNumber,
                    existing.getStatusCode()
            );
            throw new InvalidOrderStatusTransitionException(
                    "Order event already exists for shipmentNumber=" + shipmentNumber
            );
        });
    }

    /**
     * Walidacja: niedozwolone statusy początkowe
     */
    public void validateInitialStatusAllowed(String shipmentNumber,
                                             int statusCode) {

        if (statusCode == OrderStatus.PACKED_AND_SHIPPED.getCode()
                || statusCode == OrderStatus.OUT_FOR_DELIVERY.getCode()) {

            log.warn(
                    "validator - invalid initial status blocked [shipmentNumber={}, statusCode={}]",
                    shipmentNumber,
                    statusCode
            );

            throw new InvalidOrderStatusTransitionException(
                    "Initial order event cannot have shipment-related status"
            );
        }
    }

    /**
     * Walidacja: zamówienie musi istnieć
     */
    public OrderEvent validateOrderExists(String shipmentNumber,
                                          Optional<OrderEvent> event) {

        return event.orElseThrow(() -> {
            log.warn(
                    "validator - order not found [shipmentNumber={}]",
                    shipmentNumber
            );
            return new OrderNotFoundException(
                    "Order not found for shipmentNumber=" + shipmentNumber
            );
        });
    }

    /**
     * Walidacja: status już docelowy (PACKED_AND_SHIPPED)
     */
    public boolean validateAlreadyPackedAndShipped(String shipmentNumber,
                                                   int currentStatus) {

        if (currentStatus == OrderStatus.PACKED_AND_SHIPPED.getCode()) {
            log.info(
                    "validator - order already packed and shipped [shipmentNumber={}, statusCode={}]",
                    shipmentNumber,
                    currentStatus
            );
            return true;
        }
        return false;
    }

    /**
     * Walidacja: wymagany status poprzedni do OUT_FOR_DELIVERY
     */
    public void validateCanBeOutForDelivery(String shipmentNumber,
                                            int currentStatus) {

        if (currentStatus != OrderStatus.PACKED_AND_SHIPPED.getCode()) {
            log.warn(
                    "validator - invalid status transition blocked [shipmentNumber={}, currentStatusCode={}, requiredStatusCode={}]",
                    shipmentNumber,
                    currentStatus,
                    OrderStatus.PACKED_AND_SHIPPED.getCode()
            );

            throw new InvalidOrderStatusTransitionException(
                    "Order must be PACKED_AND_SHIPPED before OUT_FOR_DELIVERY"
            );
        }
    }
}
