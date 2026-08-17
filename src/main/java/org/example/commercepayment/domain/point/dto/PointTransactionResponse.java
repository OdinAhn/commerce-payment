package org.example.commercepayment.domain.point.dto;

import org.example.commercepayment.domain.point.entity.PointTransaction;

import java.time.LocalDateTime;

// 포인트 거래 내역 응답.
// amount는 부호 있는 값 그대로 내려준다 — 클라이언트가 타입 보고 부호를 다시 계산할 필요가 없다.
public record PointTransactionResponse(
        Long transactionId,
        String type,
        String typeDescription,
        int amount,
        int balanceAfter,
        Long paymentId,
        LocalDateTime createdAt
) {
    public static PointTransactionResponse from(PointTransaction tx) {
        return new PointTransactionResponse(
                tx.getId(),
                tx.getTransactionType().name(),
                tx.getTransactionType().getDescription(),
                tx.getAmount(),
                tx.getBalanceAfter(),
                tx.getPayment() != null ? tx.getPayment().getId() : null,
                tx.getCreatedAt()
        );
    }
}
