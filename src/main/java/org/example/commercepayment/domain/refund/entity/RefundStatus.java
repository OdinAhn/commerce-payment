package org.example.commercepayment.domain.refund.entity;

public enum RefundStatus {
    COMPLETED {
        @Override
        public void validateTransitionTo(RefundStatus newStatus) {
            if (this == newStatus) {
                throw new IllegalStateException("이미 환불 완료(COMPLETED) 상태입니다.");
            }
            throw new IllegalStateException("환불 완료된 건은 다른 상태로 변경할 수 없습니다.");
        }
    },
    FAILED {
        @Override
        public void validateTransitionTo(RefundStatus newStatus) {
            // 실패 상태에서는 어떤 상태로든 변경 불가능
            throw new IllegalStateException("이미 실패 처리된 환불 건의 상태는 변경할 수 없습니다.");
        }
    };

    public abstract void validateTransitionTo(RefundStatus newStatus);
}
