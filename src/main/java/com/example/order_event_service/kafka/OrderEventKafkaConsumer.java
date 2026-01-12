package com.example.order_event_service.kafka;

import com.example.order_event_service.dto.OrderRequestDto;
import com.example.order_event_service.dto.OrderStatusUpdateDto;
import com.example.order_event_service.exception.InvalidOrderStatusTransitionException;
import com.example.order_event_service.service.OrderEventFacade;
import org.apache.kafka.clients.consumer.ConsumerRecord;
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
    public void consume(ConsumerRecord<String, Object> record) {

        Object payload = record.value();

        log.info(
                "Kafka consumer - received event [key={}, payloadType={}]",
                record.key(),
                payload.getClass().getSimpleName()
        );

        try {
            if (payload instanceof OrderRequestDto request) {
                facade.processOrderEvent(request);
                return;
            }

            if (payload instanceof OrderStatusUpdateDto statusUpdate) {
                facade.processStatusUpdate(statusUpdate);
                return;
            }

            log.warn(
                    "Kafka consumer - unsupported payload type [{}]",
                    payload.getClass()
            );

        } catch (InvalidOrderStatusTransitionException ex) {
            // ❗ BŁĄD BIZNESOWY – EVENT JEST ZŁY, SYSTEM JEST OK
            log.warn(
                    "Business rule violation – event ignored [key={}, reason={}]",
                    record.key(),
                    ex.getMessage()
            );
            // NIE RZUCAMY WYJĄTKU → offset idzie dalej

        } catch (Exception ex) {
            // ❗ BŁĄD TECHNICZNY – TU KAFKA MA PRAWO ROBIĆ RETRY
            log.error(
                    "Technical error while processing Kafka event [key={}]",
                    record.key(),
                    ex
            );
            throw ex; // retry / backoff / DLQ
        }
    }
}
