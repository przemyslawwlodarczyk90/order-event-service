package com.example.order_event_service.service.handler;

import com.example.order_event_service.entity.OrderEvent;
import com.example.order_event_service.repository.OrderEventRepository;
import com.example.order_event_service.util.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetOrderEventsHandlerTest {


    @Mock
    private OrderEventRepository repository;


    @Mock
    private Validator validator;


    @InjectMocks
    private GetOrderEventsHandler handler;

    @Test
    void shouldReturnOrderEvents_whenOrderExists() {
        // given – istnieją eventy dla shipmentu
        String shipmentNumber = "SHIP-123";

        OrderEvent lastEvent = new OrderEvent();

        when(repository.findTopByShipmentNumberOrderByReceivedAtDesc(shipmentNumber))
                .thenReturn(Optional.of(lastEvent));

        when(repository.findAllByShipmentNumberOrderByReceivedAtAsc(shipmentNumber))
                .thenReturn(List.of(new OrderEvent(), new OrderEvent()));

        // when – wywołanie handlera
        List<OrderEvent> result = handler.handle(shipmentNumber);

        // then – zwrócona lista eventów
        assertEquals(2, result.size());

        // walidacja istnienia zamówienia musi się wykonać
        verify(validator).validateOrderExists(
                shipmentNumber,
                Optional.of(lastEvent)
        );

        // handler deleguje do repozytorium
        verify(repository).findAllByShipmentNumberOrderByReceivedAtAsc(shipmentNumber);
    }

    @Test
    void shouldThrowException_whenOrderDoesNotExist() {
        // given – brak eventów dla shipmentu
        String shipmentNumber = "SHIP-404";

        when(repository.findTopByShipmentNumberOrderByReceivedAtDesc(shipmentNumber))
                .thenReturn(Optional.empty());

        // validator rzuca wyjątek przy braku zamówienia
        doThrow(RuntimeException.class)
                .when(validator)
                .validateOrderExists(eq(shipmentNumber), any());

        // when + then – spodziewamy się wyjątku
        assertThrows(RuntimeException.class, () ->
                handler.handle(shipmentNumber)
        );

        // nie wolno pobierać listy eventów, jeśli walidacja nie przeszła
        verify(repository, never())
                .findAllByShipmentNumberOrderByReceivedAtAsc(any());
    }
}
