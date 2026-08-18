package org.example.commercepayment.domain.payment.port;

public record PaymentGatewayResponse(
        String id,
        String status,
        int totalAmount
) {}