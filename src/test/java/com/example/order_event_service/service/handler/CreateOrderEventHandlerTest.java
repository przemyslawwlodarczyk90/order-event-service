package com.example.order_event_service.service.handler;

import com.example.order_event_service.dto.OrderRequestDto;
import com.example.order_event_service.entity.OrderEvent;
import com.example.order_event_service.mapper.OrderEventMapper;
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
class CreateOrderEventHandlerTest {


    @Mock
    private OrderEventRepository repository;

    @Mock
    private OrderEventMapper mapper;

    @Mock
    private Validator validator;


    @InjectMocks
    private CreateOrderEventHandler handler;

    @Test
    void shouldCreateOrderEvent_whenValidRequest() {

        // given – przygotowanie danych wejściowych
        OrderRequestDto request = new OrderRequestDto();
        request.setShipmentNumber("SHIP-123");
        request.setStatusCode(10);

        OrderEvent mappedEvent = new OrderEvent();
        mappedEvent.setShipmentNumber("SHIP-123");
        mappedEvent.setStatusCode(10);

        // repository nic nie znajduje – order jeszcze nie istnieje
        when(repository.findByShipmentNumber("SHIP-123"))
                .thenReturn(Optional.empty());

        // mapper zwraca encję na podstawie dto
        when(mapper.toEntity(request))
                .thenReturn(mappedEvent);

        // when – wywołanie testowanej metody
        OrderEvent result = handler.handle(request);

        // then – sprawdzenie rezultatu
        assertNotNull(result);
        assertEquals("SHIP-123", result.getShipmentNumber());
        assertEquals(10, result.getStatusCode());

        // weryfikujemy, że kluczowe kroki zostały wykonane
        verify(validator).validateOrderDoesNotExist(
                eq("SHIP-123"),
                any()
        );
        verify(validator).validateInitialStatusAllowed("SHIP-123", 10);
        verify(repository).save(mappedEvent);
    }

    @Test
    void shouldThrowException_whenOrderAlreadyExists() {
        // given
        OrderRequestDto request = new OrderRequestDto();
        request.setShipmentNumber("SHIP-123");
        request.setStatusCode(10);

        OrderEvent existingEvent = new OrderEvent();

        // repository zwraca istniejący event
        when(repository.findByShipmentNumber("SHIP-123"))
                .thenReturn(Optional.of(existingEvent));

        // validator rzuca wyjątek przy duplikacie
        doThrow(RuntimeException.class)
                .when(validator)
                .validateOrderDoesNotExist(eq("SHIP-123"), any());

        // when + then – spodziewamy się wyjątku
        assertThrows(RuntimeException.class, () -> handler.handle(request));

        // zapis nie powinien się wydarzyć
        verify(repository, never()).save(any());
    }

    @Test
    void shouldThrowException_whenInitialStatusIsInvalid() {
        // given
        OrderRequestDto request = new OrderRequestDto();
        request.setShipmentNumber("SHIP-123");
        request.setStatusCode(80); // niedozwolony status początkowy

        when(repository.findByShipmentNumber("SHIP-123"))
                .thenReturn(Optional.empty());

        // validator blokuje status początkowy
        doThrow(RuntimeException.class)
                .when(validator)
                .validateInitialStatusAllowed("SHIP-123", 80);

        // when + then
        assertThrows(RuntimeException.class, () -> handler.handle(request));

        // mapper i save nie powinny się uruchomić
        verify(mapper, never()).toEntity(any());
        verify(repository, never()).save(any());
    }
}
