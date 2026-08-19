package org.example.commercepayment.domain.point.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.commercepayment.domain.member.entity.Member;
import org.example.commercepayment.domain.payment.entity.Payment;
import org.example.commercepayment.global.entity.BaseTimeEntity;

// 포인트 원장. 모든 포인트 변동이 한 줄씩 기록되며, 한번 저장된 행은 수정/삭제하지 않는다.
// 불변식: members.point == SUM(points.amount)
@Entity
@Table(name = "points")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PointTransaction extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 20)
    private PointTransactionType transactionType;

    // 부호 있는 금액 (사용 -3000, 적립 +300). SUM(amount)이 곧 회원 잔액이 된다.
    @Column(nullable = false)
    private int amount;

    private PointTransaction(Member member, Payment payment,
                             PointTransactionType transactionType, int amount) {
        this.member = member;
        this.payment = payment;
        this.transactionType = transactionType;
        this.amount = amount;
    }

    // positiveAmount는 항상 양수로 넘긴다. 부호는 타입이 결정한다.
    public static PointTransaction record(Member member, Payment payment,
                                          PointTransactionType type, int positiveAmount) {
        return new PointTransaction(member, payment, type, type.applySign(positiveAmount));
    }
}
