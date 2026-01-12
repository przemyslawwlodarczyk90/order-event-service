package com.example.order_event_service.kafka;

import com.example.order_event_service.dto.OrderRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventKafkaProducer {

    private static final Logger log =
            LoggerFactory.getLogger(OrderEventKafkaProducer.class);

    private static final String TOPIC = "order-events";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public OrderEventKafkaProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOrderEvent(String key, Object payload) {
        log.info(
                "Kafka producer - sending event [key={}, payloadType={}]",
                key,
                payload.getClass().getSimpleName()
        );
        kafkaTemplate.send(TOPIC, key, payload);
    }
}
