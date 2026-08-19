package org.example.commercepayment.domain.refund.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.commercepayment.global.entity.BaseTimeEntity;

@Entity
@Getter
@Table(name = "refund_items")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RefundItem extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "refund_id", nullable = false)
    private Refund refund;

    // TODO: 추후 OrderItem 객체 참조가 필요해지면 아래 주석을 풀고 기존 orderItemId를 대체
    /*
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id", nullable = false)
    private OrderItem orderItem;
    */

    // 임시: OrderItem 엔티티 부재로 인한 ID 값(Soft Reference) 매핑
    @Column(name = "order_item_id", nullable = false)
    private Long orderItemId;

    @Column(name = "refund_quantity", nullable = false)
    private int refundQuantity;

    @Column(name = "point_refund_amount", nullable = false)
    private int pointRefundAmount;

    @Column(name = "pg_refund_amount", nullable = false)
    private int pgRefundAmount;

    // 생성자 및 빌더는 private으로 캡슐화
    @Builder(access = AccessLevel.PRIVATE)
    private RefundItem(Long orderItemId, int refundQuantity, int pointRefundAmount, int pgRefundAmount) {
        // TODO: 객체 참조 변경 시 orderItemId 파라미터를 OrderItem 객체로 변경
        this.orderItemId = orderItemId;
        this.refundQuantity = refundQuantity;
        this.pointRefundAmount = pointRefundAmount;
        this.pgRefundAmount = pgRefundAmount;
    }

    // 정적 팩토리 메서드를 통해서만 객체 생성
    public static RefundItem create(Long orderItemId, int refundQuantity, int pointRefundAmount, int pgRefundAmount) {
        if (refundQuantity <= 0) {
            throw new IllegalArgumentException("환불 수량은 1개 이상이어야 합니다.");
        }
        
        return RefundItem.builder()
                .orderItemId(orderItemId)
                .refundQuantity(refundQuantity)
                .pointRefundAmount(pointRefundAmount)
                .pgRefundAmount(pgRefundAmount)
                .build();
    }

    // 연관관계 편의 메서드 (Refund 엔티티에서 호출)
    void assignRefund(Refund refund) {
        this.refund = refund;
    }
}
