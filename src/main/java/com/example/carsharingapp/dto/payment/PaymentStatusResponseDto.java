package com.example.carsharingapp.dto.payment;

import com.example.carsharingapp.model.Payment;
import lombok.Data;

@Data
public class PaymentStatusResponseDto {
    private String sessionId;
    private Payment.Status status;
}
