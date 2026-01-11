package com.example.order_event_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class InvalidOrderStatusTransitionException
        extends RuntimeException {
    public InvalidOrderStatusTransitionException(String message) {
        super(message);
    }
}
