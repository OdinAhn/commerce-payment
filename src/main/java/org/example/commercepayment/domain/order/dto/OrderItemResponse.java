package org.example.commercepayment.domain.order.dto;

public record OrderItemResponse(
        Long orderItemId,
        Long productId,
        String productName,
        int orderPrice,
        int quantity,
        int subtotal) {

}