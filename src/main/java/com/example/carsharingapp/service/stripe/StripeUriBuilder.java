package com.example.carsharingapp.service.stripe;

import org.springframework.web.util.UriComponentsBuilder;

public class StripeUriBuilder {
    public static final String BASE_URL = "http://localhost:8080";
    public static final String SUCCESS_PATH = "/payments/success";
    public static final String CANCEL_PATH = "/payments/cancel";

    public static String buildSuccessUrl() {
        return UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .path(SUCCESS_PATH)
                .queryParam("session_id", "{CHECKOUT_SESSION_ID}")
                .build()
                .toUriString();
    }

    public static String buildCancelUrl() {
        return UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .path(CANCEL_PATH)
                .build()
                .toUriString();
    }
}
