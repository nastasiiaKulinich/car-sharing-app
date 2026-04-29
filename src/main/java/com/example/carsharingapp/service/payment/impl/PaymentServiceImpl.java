package com.example.carsharingapp.service.payment.impl;

import com.example.carsharingapp.dto.payment.CreatePaymentRequestDto;
import com.example.carsharingapp.dto.payment.PaymentDetailedResponseDto;
import com.example.carsharingapp.dto.payment.PaymentResponseDto;
import com.example.carsharingapp.dto.payment.PaymentStatusResponseDto;
import com.example.carsharingapp.exception.EntityNotFoundException;
import com.example.carsharingapp.exception.PaymentException;
import com.example.carsharingapp.mapper.PaymentMapper;
import com.example.carsharingapp.model.Payment;
import com.example.carsharingapp.model.Rental;
import com.example.carsharingapp.repository.PaymentRepository;
import com.example.carsharingapp.repository.RentalRepository;
import com.example.carsharingapp.service.notification.NotificationService;
import com.example.carsharingapp.service.notification.template.NotificationTemplates;
import com.example.carsharingapp.service.notification.template.NotificationType;
import com.example.carsharingapp.service.payment.PaymentService;
import com.example.carsharingapp.service.payment.strategy.CalculationService;
import com.example.carsharingapp.service.payment.strategy.CalculationServiceStrategy;
import com.example.carsharingapp.service.stripe.StripeService;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {
    private final RentalRepository rentalRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final CalculationServiceStrategy calculationServiceStrategy;
    private final StripeService stripeService;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public PaymentResponseDto createPaymentSession(Long userId,
                                                   CreatePaymentRequestDto requestDto) {
        Rental rental = rentalRepository.findByIdAndUserId(requestDto.getRentalId(), userId)
                .orElseThrow(() -> new EntityNotFoundException("Can't find rental by id: "
                        + requestDto.getRentalId() + " and user id: " + userId));
        CalculationService calculationService = calculationServiceStrategy
                .getCalculationService(requestDto.getPaymentType());
        BigDecimal amount = calculationService.calculateAmount(rental);
        SessionCreateParams sessionParams = stripeService.createSessionParams(amount);
        Session session = stripeService.makeSession(sessionParams);
        Payment payment = preparePayment(session, requestDto, rental, amount);
        return paymentMapper.toDto(paymentRepository.save(payment));
    }

    @Override
    @Transactional
    public PaymentStatusResponseDto handleSuccess(String sessionId) {
        Payment payment = findPaymentBySessionId(sessionId);
        if (payment.getStatus() == Payment.Status.PAID) {
            return paymentMapper.toStatusDto(payment);
        }
        if (!stripeService.isSessionPaid(sessionId)) {
            throw new PaymentException("Payment for session id: " + sessionId
                    + " is not successful!");
        }
        payment.setStatus(Payment.Status.PAID);
        Payment savedPayment = paymentRepository.save(payment);
        String message = NotificationTemplates.getTemplate(NotificationType.PAYMENT_SUCCESS,
                savedPayment.getId(),
                savedPayment.getAmountToPay(),
                savedPayment.getRental().getId());
        notificationService.sendMessageAdmin(message);
        return paymentMapper.toStatusDto(savedPayment);
    }

    @Override
    @Transactional
    public PaymentStatusResponseDto handleCancel(String sessionId) {
        Payment payment = findPaymentBySessionId(sessionId);
        if (payment.getStatus() == Payment.Status.CANCELED) {
            return paymentMapper.toStatusDto(payment);
        }
        if (payment.getStatus() == Payment.Status.PENDING) {
            payment.setStatus(Payment.Status.CANCELED);
            Payment savedPayment = paymentRepository.save(payment);
            String message = NotificationTemplates.getTemplate(NotificationType.PAYMENT_FAILED,
                    savedPayment.getId(),
                    savedPayment.getRental().getId());
            notificationService.sendMessageAdmin(message);
            return paymentMapper.toStatusDto(savedPayment);
        }
        throw new PaymentException("Can't cancel payment with status: "
                + payment.getStatus());
    }

    @Override
    public List<PaymentDetailedResponseDto> findAll(Long authUserId, boolean isAdmin,
                                                    Long userId, Pageable pageable) {
        Long targetUserId = isAdmin ? userId : authUserId;

        List<Payment> payments = paymentRepository
                .findByRentalUserId(targetUserId, pageable);

        return payments.stream()
                .map(paymentMapper::toDetailedDto)
                .toList();
    }

    private Payment findPaymentBySessionId(String sessionId) {
        return paymentRepository.findBySessionId(sessionId).orElseThrow(
                () -> new EntityNotFoundException("Can't find payment by sessionId: "
                        + sessionId));
    }

    private Payment preparePayment(Session session,
                                          CreatePaymentRequestDto requestDto,
                                          Rental rental,
                                          BigDecimal amount) {
        return new Payment()
                .setStatus(Payment.Status.PENDING)
                .setType(requestDto.getPaymentType())
                .setRental(rental)
                .setSessionUrl(session.getUrl())
                .setSessionId(session.getId())
                .setAmountToPay(amount);
    }
}
