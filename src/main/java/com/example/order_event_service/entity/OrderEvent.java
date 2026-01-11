package com.example.order_event_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "order_event_audit")
public class OrderEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "shipment_number", nullable = false, length = 64)
    private String shipmentNumber;


    @Column(name = "recipient_email", nullable = false, length = 320)
    private String recipientEmail;


    @Column(name = "recipient_country_code", nullable = false, length = 2)
    private String recipientCountryCode;


    @Column(name = "sender_country_code", nullable = false, length = 2)
    private String senderCountryCode;


    @Column(name = "status_code", nullable = false)
    private int statusCode;


    @Column(name = "received_at", nullable = false)
    private Instant receivedAt = Instant.now();
}
