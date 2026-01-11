package com.example.order_event_service.repository;

import com.example.order_event_service.entity.OrderEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderEventRepository extends JpaRepository<OrderEvent, Long> {

    Optional<OrderEvent> findByShipmentNumber(String shipmentNumber);

}
