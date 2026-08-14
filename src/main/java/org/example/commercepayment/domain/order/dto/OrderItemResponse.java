package org.example.commercepayment.domain.order.dto;

public record OrderItemResponse(
        String productName,
        int orderPrice,
        int quantity
) {}
