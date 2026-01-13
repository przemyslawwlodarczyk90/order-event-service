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
class PackAndShipOrderHandlerTest {


    @Mock
    private OrderEventRepository repository;


    @Mock
    private Validator validator;

    // testowany handler
    @InjectMocks
    private PackAndShipOrderHandler handler;

    @Test
    void shouldAppendPackedAndShippedEvent_whenPreviousStatusIsAccepted() {
        // given – istnieje zamówienie w statusie ORDER_ACCEPTED
        String shipmentNumber = "SHIP-123";

        OrderEvent lastEvent = new OrderEvent();
        lastEvent.setShipmentNumber(shipmentNumber);
        lastEvent.setRecipientEmail("test@test.com");
        lastEvent.setRecipientCountryCode("PL");
        lastEvent.setSenderCountryCode("DE");
        lastEvent.setStatusCode(OrderStatus.ORDER_ACCEPTED.getCode());

        when(repository.findTopByShipmentNumberOrderByReceivedAtDesc(shipmentNumber))
                .thenReturn(Optional.of(lastEvent));

        when(validator.validateOrderExists(eq(shipmentNumber), any()))
                .thenReturn(lastEvent);

        // validator mówi: jeszcze nie jest packed
        when(validator.validateAlreadyPackedAndShipped(
                shipmentNumber,
                OrderStatus.ORDER_ACCEPTED.getCode()
        )).thenReturn(false);

        // when – wykonanie handlera
        Optional<OrderEvent> result = handler.handle(shipmentNumber);

        // then – nowy event został utworzony
        assertTrue(result.isPresent());
        assertEquals(OrderStatus.PACKED_AND_SHIPPED.getCode(),
                result.get().getStatusCode());

        // zapis do repozytorium musi nastąpić
        verify(repository).save(any(OrderEvent.class));
    }

    @Test
    void shouldReturnEmpty_whenOrderAlreadyPackedAndShipped() {
        // given – ostatni status to już PACKED_AND_SHIPPED
        String shipmentNumber = "SHIP-123";

        OrderEvent lastEvent = new OrderEvent();
        lastEvent.setShipmentNumber(shipmentNumber);
        lastEvent.setStatusCode(OrderStatus.PACKED_AND_SHIPPED.getCode());

        when(repository.findTopByShipmentNumberOrderByReceivedAtDesc(shipmentNumber))
                .thenReturn(Optional.of(lastEvent));

        when(validator.validateOrderExists(eq(shipmentNumber), any()))
                .thenReturn(lastEvent);

        // validator wykrywa, że status jest już ustawiony
        when(validator.validateAlreadyPackedAndShipped(
                shipmentNumber,
                OrderStatus.PACKED_AND_SHIPPED.getCode()
        )).thenReturn(true);

        // when
        Optional<OrderEvent> result = handler.handle(shipmentNumber);

        // then – brak nowego eventu
        assertTrue(result.isEmpty());

        // zapis nie może się wydarzyć
        verify(repository, never()).save(any());
    }

    @Test
    void shouldThrowException_whenOrderDoesNotExist() {
        // given – brak zamówienia w systemie
        String shipmentNumber = "SHIP-404";

        when(repository.findTopByShipmentNumberOrderByReceivedAtDesc(shipmentNumber))
                .thenReturn(Optional.empty());

        // validator rzuca wyjątek
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
