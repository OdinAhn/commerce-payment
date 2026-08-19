package org.example.commercepayment.domain.refund.repository;

import org.example.commercepayment.domain.refund.entity.RefundItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RefundItemRepository extends JpaRepository<RefundItem, Long> {

    /**
     * 특정 주문 상품에 대해 환불 처리 완료(COMPLETED)된 총 수량
     *
     */
    // TODO: 엔티티가 OrderItem 객체 참조로 변경되면 쿼리 내의 ri.orderItemId를 ri.orderItem.id 로 수정
    @Query("SELECT COALESCE(SUM(ri.refundQuantity), 0) " +
            "FROM RefundItem ri " +
            "WHERE ri.orderItemId = :orderItemId " +
            "AND ri.refund.status = 'COMPLETED'")
    int sumRefundedQuantityByOrderItemId(@Param("orderItemId") Long orderItemId);

    // TODO: 엔티티가 Payment 객체 참조로 변경되면 쿼리 내의 ri.refund.paymentId를 ri.refund.payment.id 로 수정
    @Query("SELECT COALESCE(SUM(ri.refundQuantity), 0) " +
            "FROM RefundItem ri " +
            "WHERE ri.refund.paymentId = :paymentId " +
            "AND ri.refund.status = 'COMPLETED'")
    int sumRefundedQuantityByPaymentId(@Param("paymentId") Long paymentId);

    /**
     * 특정 주문 상품에 대해 환불된 총 포인트 금액
     */
    @Query("SELECT COALESCE(SUM(ri.pointRefundAmount), 0)" +
            "FROM RefundItem ri " +
            "WHERE ri.orderItemId = :orderItemId " +
            "AND ri.refund.status = 'COMPLETED'")
    int sumRefundedPointAmountByOrderItemId(@Param("orderItemId") Long orderItemId);

    /**
     * 특정 주문 상품에 대해 환불된 총 PG 결제 금액
     */
    @Query("SELECT COALESCE(SUM(ri.pgRefundAmount), 0) " +
            "FROM RefundItem ri " +
            "WHERE ri.orderItemId = :orderItemId " +
            "AND ri.refund.status = 'COMPLETED'")
    int sumRefundedPgAmountByOrderItemId(@Param("orderItemId") Long orderItemId);
}
