package com.example.carsharingapp.service.payment.strategy;

import com.example.carsharingapp.model.Payment;
import com.example.carsharingapp.model.Rental;
import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import org.springframework.stereotype.Service;

@Service
public class DefaultPaymentCalculationService implements CalculationService {
    @Override
    public BigDecimal calculateAmount(Rental rental) {
        long rentalDays = ChronoUnit.DAYS.between(rental.getRentalDate(), rental.getReturnDate());
        return rental.getCar().getDailyFee().multiply(BigDecimal.valueOf(rentalDays));
    }

    @Override
    public Payment.Type getPaymentType() {
        return Payment.Type.PAYMENT;
    }
}
