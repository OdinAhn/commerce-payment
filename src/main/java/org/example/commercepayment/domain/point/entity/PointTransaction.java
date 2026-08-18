package org.example.commercepayment.domain.point.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.commercepayment.domain.member.entity.Member;
import org.example.commercepayment.domain.payment.entity.Payment;
import org.example.commercepayment.global.entity.BaseTimeEntity;

// 포인트 원장. 모든 포인트 변동이 한 줄씩 기록되며, 한번 저장된 행은 수정/삭제하지 않는다.
@Entity
@Table(
        name = "point_transactions",   // transaction은 예약어라 회피
        indexes = {
                @Index(name = "idx_point_tx_member", columnList = "member_id, id"),
                @Index(name = "idx_point_tx_payment", columnList = "payment_id")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointTransaction extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    // 결제와 무관한 거래(초기 지급 등)도 있어서 nullable
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 20)
    private PointTransactionType transactionType;

    // 부호 있는 금액 (사용 -3000, 적립 +300). SUM(amount)이 곧 회원 잔액이 된다.
    @Column(nullable = false)
    private int amount;

    // 거래 직후 잔액. 정합성이 깨졌을 때 어느 거래부터 어긋났는지 추적용.
    @Column(name = "balance_after", nullable = false)
    private int balanceAfter;

    private PointTransaction(Member member, Payment payment,
                             PointTransactionType transactionType,
                             int amount, int balanceAfter) {
        this.member = member;
        this.payment = payment;
        this.transactionType = transactionType;
        this.amount = amount;
        this.balanceAfter = balanceAfter;
    }

    // positiveAmount는 항상 양수로 넘긴다. 부호는 타입이 결정한다.
    public static PointTransaction record(Member member, Payment payment,
                                          PointTransactionType type,
                                          int positiveAmount, int balanceAfter) {
        return new PointTransaction(member, payment, type,
                type.applySign(positiveAmount), balanceAfter);
    }
}
