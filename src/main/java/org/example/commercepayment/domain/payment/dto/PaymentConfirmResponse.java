package org.example.commercepayment.domain.payment.dto;

public record PaymentConfirmResponse(
        Long paymentId,
        Long orderId,
        int amount,
        String paymentStatus,
        String orderStatus,
        String message
) {}