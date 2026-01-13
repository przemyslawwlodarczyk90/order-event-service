package com.example.order_event_service.service.handler;

import com.example.order_event_service.domain.OrderStatus;
import com.example.order_event_service.entity.OrderEvent;
import com.example.order_event_service.repository.OrderEventRepository;
import com.example.order_event_service.util.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutForDeliveryOrderHandlerTest {

    @Mock
    private OrderEventRepository repository;


    @Mock
    private Validator validator;


    @InjectMocks
    private OutForDeliveryOrderHandler handler;

    @Test
    void shouldAppendOutForDeliveryEvent_whenPreviousStatusIsPacked() {
        // given – istnieje zamówienie w statusie PACKED_AND_SHIPPED
        String shipmentNumber = "SHIP-123";

        OrderEvent lastEvent = new OrderEvent();
        lastEvent.setShipmentNumber(shipmentNumber);
        lastEvent.setRecipientEmail("test@test.com");
        lastEvent.setRecipientCountryCode("PL");
        lastEvent.setSenderCountryCode("DE");
        lastEvent.setStatusCode(OrderStatus.PACKED_AND_SHIPPED.getCode());

        when(repository.findTopByShipmentNumberOrderByReceivedAtDesc(shipmentNumber))
                .thenReturn(Optional.of(lastEvent));

        // validator zwraca istniejący event (brak wyjątku)
        when(validator.validateOrderExists(eq(shipmentNumber), any()))
                .thenReturn(lastEvent);

        // when – wykonanie logiki
        Optional<OrderEvent> result = handler.handle(shipmentNumber);

        // then – nowy event został utworzony
        assertTrue(result.isPresent());
        assertEquals(OrderStatus.OUT_FOR_DELIVERY.getCode(),
                result.get().getStatusCode());

        // zapis do repozytorium musi nastąpić
        verify(repository).save(any(OrderEvent.class));

        // walidacje muszą się wykonać
        verify(validator).validateCanBeOutForDelivery(
                shipmentNumber,
                OrderStatus.PACKED_AND_SHIPPED.getCode()
        );
    }

    @Test
    void shouldThrowException_whenPreviousStatusIsNotPacked() {
        // given – istnieje zamówienie, ale w złym statusie
        String shipmentNumber = "SHIP-123";

        OrderEvent lastEvent = new OrderEvent();
        lastEvent.setShipmentNumber(shipmentNumber);
        lastEvent.setStatusCode(OrderStatus.ORDER_ACCEPTED.getCode());

        when(repository.findTopByShipmentNumberOrderByReceivedAtDesc(shipmentNumber))
                .thenReturn(Optional.of(lastEvent));

        when(validator.validateOrderExists(eq(shipmentNumber), any()))
                .thenReturn(lastEvent);

        // validator blokuje przejście stanu
        doThrow(RuntimeException.class)
                .when(validator)
                .validateCanBeOutForDelivery(
                        shipmentNumber,
                        OrderStatus.ORDER_ACCEPTED.getCode()
                );

        // when + then – spodziewamy się wyjątku
        assertThrows(RuntimeException.class,
                () -> handler.handle(shipmentNumber));

        // zapis nie może się wydarzyć
        verify(repository, never()).save(any());
    }

    @Test
    void shouldThrowException_whenOrderDoesNotExist() {
        // given – brak zamówienia w systemie
        String shipmentNumber = "SHIP-404";

        when(repository.findTopByShipmentNumberOrderByReceivedAtDesc(shipmentNumber))
                .thenReturn(Optional.empty());

        // validator rzuca wyjątek przy braku zamówienia
        doThrow(RuntimeException.class)
                .when(validator)
                .validateOrderExists(eq(shipmentNumber), any());

        // when + then
        assertThrows(RuntimeException.class,
                () -> handler.handle(shipmentNumber));

        // żadnych zapisów
        verify(repository, never()).save(any());
    }
}
