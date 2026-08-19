package org.example.commercepayment.domain.order.dto;

import org.example.commercepayment.global.error.BusinessException;
import org.example.commercepayment.global.error.ErrorCode;

import java.util.List;

// 주문 생성 요청
public record OrderCheckoutRequest(List<Long> cartItemIds, int usePoint) {

    public OrderCheckoutRequest {
        if (cartItemIds == null) {
            cartItemIds = List.of();
        }

        // 음수를 허영하면 결제금액이 주문 총액보다 커짐
        if (usePoint < 0) {
            throw new BusinessException(ErrorCode.INVALID_POINT_AMOUNT);
        }
    }
}