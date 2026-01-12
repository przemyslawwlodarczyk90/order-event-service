package com.example.order_event_service.kafka;

import com.example.order_event_service.dto.OrderRequestDto;
import com.example.order_event_service.service.OrderEventFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventKafkaConsumer {

    private static final Logger log =
            LoggerFactory.getLogger(OrderEventKafkaConsumer.class);

    private final OrderEventFacade facade;

    public OrderEventKafkaConsumer(OrderEventFacade facade) {
        this.facade = facade;
    }

    @KafkaListener(
            topics = "order-events",
            groupId = "order-event-service-group",
            concurrency = "2"
    )
    public void consume(Object payload) {

        log.info(
                "Kafka consumer - received event [payloadType={}]",
                payload.getClass().getSimpleName()
        );

        if (payload instanceof OrderRequestDto request) {
            facade.processOrderEvent(request);
            return;
        }

        log.warn(
                "Kafka consumer - unsupported payload type [{}]",
                payload.getClass()
        );
    }
}
