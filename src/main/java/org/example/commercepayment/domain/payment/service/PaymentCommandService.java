package org.example.commercepayment.domain.payment.service;

import org.example.commercepayment.domain.order.entity.Order;
import org.example.commercepayment.domain.order.entity.OrderStatus;
import org.example.commercepayment.domain.payment.dto.PaymentConfirmResponse;
import org.example.commercepayment.domain.payment.entity.FailReason;
import org.example.commercepayment.domain.payment.entity.Payment;
import lombok.RequiredArgsConstructor;
import org.example.commercepayment.domain.point.service.PointService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentCommandService {

    private static final double POINT_EARN_RATE = 0.01;

    private final PaymentService paymentService;
    private final PointService pointService;

    @Transactional
    public void failPaymentAndOrder(Long orderId, FailReason reason) {
        Payment payment = paymentService.findByOrderIdWithOrder(orderId);
        Order order = payment.getOrder();

        paymentService.failPayment(payment, reason);

        // 포인트를 이미 사용 처리했었다면 복구 (전액 카드 결제라 사용액이 0이면 스킵)
        if (payment.getPointUsedAmount() > 0) {
            pointService.restoreUsed(order.getMemberId(), payment, payment.getPointUsedAmount());
        }

        order.transitTo(OrderStatus.CANCELLED);
        order.getOrderItems().forEach(item ->
                item.getProduct().restoreStock(item.getQuantity()));
    }

    @Transactional
    public PaymentConfirmResponse approvePaymentAndOrder(Long orderId) {
        Payment payment = paymentService.findByOrderIdWithOrder(orderId);
        Order order = payment.getOrder();

        int accruedPoint = (int) (payment.getPgAmount() * POINT_EARN_RATE);

        // 포인트 사용 처리
        if (payment.getPointUsedAmount() > 0) {
            pointService.use(order.getMemberId(), payment, payment.getPointUsedAmount());
        }

        paymentService.completePayment(payment, accruedPoint);

        // 포인트 적립 처리 (완료 후 금액 기준)
        if (accruedPoint > 0) {
            pointService.accrue(order.getMemberId(), payment, accruedPoint);
        }

        order.transitTo(OrderStatus.CONFIRMED);
        return PaymentConfirmResponse.from(payment);
    }
}