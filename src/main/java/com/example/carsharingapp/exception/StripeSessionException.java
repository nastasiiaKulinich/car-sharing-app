package com.example.carsharingapp.exception;

public class StripeSessionException extends RuntimeException {
    public StripeSessionException(String message) {
        super(message);
    }

    public StripeSessionException(String message, Throwable e) {
        super(message, e);
    }
}
