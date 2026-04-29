package com.example.carsharingapp.service.stripe;

import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import java.math.BigDecimal;

public interface StripeService {
    SessionCreateParams createSessionParams(BigDecimal amount);

    Session makeSession(SessionCreateParams params);

    boolean isSessionPaid(String sessionId);
}
