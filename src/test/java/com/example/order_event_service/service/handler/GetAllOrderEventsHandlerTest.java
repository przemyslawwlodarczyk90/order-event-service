package com.example.order_event_service.service.handler;

import com.example.order_event_service.entity.OrderEvent;
import com.example.order_event_service.repository.OrderEventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAllOrderEventsHandlerTest {


    @Mock
    private OrderEventRepository repository;


    @InjectMocks
    private GetAllOrderEventsHandler handler;

    @Test
    void shouldReturnAllOrderEvents() {
        // given – repozytorium zwraca przykładowe dane
        OrderEvent event1 = new OrderEvent();
        OrderEvent event2 = new OrderEvent();

        when(repository.findAll())
                .thenReturn(List.of(event1, event2));

        // when – wywołanie logiki handlera
        List<OrderEvent> result = handler.handle();

        // then – sprawdzamy wynik
        assertEquals(2, result.size());

        // oraz to, że handler deleguje do repozytorium
        verify(repository).findAll();
    }
}
