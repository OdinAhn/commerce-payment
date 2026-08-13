package org.example.commercepayment.domain.cart.dto;

import java.util.List;

// GET용 응답 객체
public record CartResponse(
        int totalPrice,             // 장바구니 전체 합계 금액
        List<CartItemResponse> items // 담긴 상품 목록
) {}