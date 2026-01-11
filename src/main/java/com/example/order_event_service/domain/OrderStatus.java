package com.example.order_event_service.domain;

public enum OrderStatus {

    ORDER_ACCEPTED(10, "Order accepted"),
    PACKED_AND_SHIPPED(30, "Packed and shipped"),
    OUT_FOR_DELIVERY(80, "Out for delivery");

    private final int code;
    private final String description;

    OrderStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
