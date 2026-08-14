package org.example.commercepayment.domain.order.dto;

import java.util.List;

public record CheckoutResponse(List<CheckoutItemResponse> items, int totalPrice) {

    public record CheckoutItemResponse(Long productId, String productName, int prince, int quantity, int subtotal) {
    }
}
