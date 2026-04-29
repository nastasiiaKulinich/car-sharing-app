package com.example.carsharingapp.service.payment.strategy;

import com.example.carsharingapp.model.Payment;
import com.example.carsharingapp.model.Rental;
import java.math.BigDecimal;

public interface CalculationService {
    BigDecimal calculateAmount(Rental rental);

    Payment.Type getPaymentType();
}
