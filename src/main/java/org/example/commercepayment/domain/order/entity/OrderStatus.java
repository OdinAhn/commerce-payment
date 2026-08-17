package org.example.commercepayment.domain.order.entity;

// 주문 상태 머신.
// 어떤 상태에서 어떤 상태로 갈 수 있는지를 enum이 직접 알고 있어,
// Order.transitTo()가 이걸 물어보고 잘못된 전이를 막는다.
public enum OrderStatus {

    // 주문 생성 직후. 재고는 이미 선차감된 상태.
    PENDING_PAYMENT {
        @Override
        public boolean canTransitTo(OrderStatus target) {
            // 결제 성공 → CONFIRMED / 결제 실패·회원 취소 → CANCELLED
            return target == CONFIRMED || target == CANCELLED;
        }
    },

    // 결제 완료. 전액 환불되면 취소로 넘어간다.
    CONFIRMED {
        @Override
        public boolean canTransitTo(OrderStatus target) {
            return target == CANCELLED;
        }
    },

    // 최종 상태. 여기서는 어디로도 갈 수 없다.
    // 덕분에 취소를 두 번 요청해도 재고가 두 번 복구되지 않는다.
    CANCELLED {
        @Override
        public boolean canTransitTo(OrderStatus target) {
            return false;
        }
    };

    public abstract boolean canTransitTo(OrderStatus target);
}
