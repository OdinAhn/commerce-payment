package org.example.commercepayment.domain.cart.dto;

public record CartItemResponse(
        Long id,
        Long productId,
        String productName,
        int price,
        int quantity,
        int stock,
        int itemTotalPrice // 추가: (price * quantity) 이 상품의 총 금액
) {}