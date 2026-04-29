package com.example.carsharingapp.service.payment;

import com.example.carsharingapp.dto.payment.CreatePaymentRequestDto;
import com.example.carsharingapp.dto.payment.PaymentDetailedResponseDto;
import com.example.carsharingapp.dto.payment.PaymentResponseDto;
import com.example.carsharingapp.dto.payment.PaymentStatusResponseDto;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface PaymentService {
    PaymentResponseDto createPaymentSession(Long userId,
                                            CreatePaymentRequestDto requestDto);

    PaymentStatusResponseDto handleSuccess(String sessionId);

    PaymentStatusResponseDto handleCancel(String sessionId);

    List<PaymentDetailedResponseDto> findAll(Long authUserId, boolean isAdmin,
                                             Long userId, Pageable pageable);
}
