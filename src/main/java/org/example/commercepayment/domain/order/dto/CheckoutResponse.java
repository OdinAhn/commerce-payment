package org.example.commercepayment.domain.order.dto;

import java.util.List;

public record CheckoutResponse(
        List<CheckoutItemResponse> items,
        int totalPrice,
        int availablePoint) {

    public record CheckoutItemResponse(
            Long productId,
            String productName,
            int price,
            int quantity,
            int subtotal,
            int stock,                 // 현재 재고
            boolean available) {       // 주문 가능 여부 (재고 >= 수량)
    }
}
