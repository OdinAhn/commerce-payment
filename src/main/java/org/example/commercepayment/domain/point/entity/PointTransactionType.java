package org.example.commercepayment.domain.point.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

// 포인트 거래 타입. 각 타입이 부호(+/-)를 갖고 있어 서비스에서 부호 실수를 막는다.
@Getter
@RequiredArgsConstructor
public enum PointTransactionType {

    USE("사용", -1),              // 결제 시 사용
    ACCRUE("적립", 1),            // 결제 완료 시 적립
    RESTORE_USED("사용복구", 1),   // 환불 시 사용분 반환
    RECLAIM("적립회수", -1);       // 환불 시 적립분 회수

    private final String description;
    private final int sign;

    // 양수 금액을 타입에 맞는 부호로 변환. USE.applySign(3000) = -3000
    public int applySign(int positiveAmount) {
        if (positiveAmount <= 0) {
            throw new IllegalArgumentException("거래 금액은 0보다 커야 합니다. amount=" + positiveAmount);
        }
        return sign * positiveAmount;
    }
}
