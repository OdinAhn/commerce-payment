package org.example.commercepayment.domain.refund.dto;
import org.example.commercepayment.domain.refund.entity.Refund;
import org.example.commercepayment.domain.refund.entity.RefundStatus;
import java.time.LocalDateTime;

public record RefundResponse(
    Long refundId,
    RefundStatus status,
    int totalRefundAmount,
    int pgRefundAmount,
    int pointRefundAmount,
    LocalDateTime refundedAt
) {
    public static RefundResponse from(Refund refund) {
        return new RefundResponse(
                refund.getId(),
                refund.getStatus(),
                refund.getPointRefundAmount() + refund.getPgRefundAmount(),
                refund.getPgRefundAmount(),
                refund.getPointRefundAmount(),
                refund.getCreatedAt()
        );
    }
}
