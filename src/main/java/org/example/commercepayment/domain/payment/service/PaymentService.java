package org.example.commercepayment.domain.payment.service;

import org.example.commercepayment.domain.order.entity.Order;
import org.example.commercepayment.domain.payment.entity.FailReason;
import org.example.commercepayment.domain.payment.entity.Payment;
import org.example.commercepayment.domain.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;

    // 결제 생성 — pgAmount는 Payment 생성자가 스스로 계산
    @Transactional
    public Payment createPayment(Order order, int amount, int pointUsedAmount) {
        Payment payment = Payment.builder()
                .order(order)
                .amount(amount)
                .pointUsedAmount(pointUsedAmount)
                .build();
        return paymentRepository.save(payment);
    }

    public Payment findByOrderIdWithOrder(Long orderId) {
        return paymentRepository.findByOrderIdWithOrder(orderId)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다. orderId=" + orderId));
    }

    public Payment findByOrderId(Long orderId) {
        return findByOrderIdWithOrder(orderId);
    }

    public Payment findByIdWithOrder(Long paymentId) {
        return paymentRepository.findByIdWithOrder(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다. paymentId=" + paymentId));
    }

    public Payment findByPortonePaymentId(String portonePaymentId) {
        return paymentRepository.findByPortonePaymentId(portonePaymentId)
                .orElseThrow(() -> new IllegalArgumentException("결제 정보를 찾을 수 없습니다. portonePaymentId=" + portonePaymentId));
    }

    @Transactional
    public void completePayment(Payment payment, int accruedPoint) {
        payment.complete(accruedPoint);
    }

    @Transactional
    public void failPayment(Payment payment, FailReason reason) {
        payment.fail(reason);
    }

    @Transactional
    public void markFailed(Long orderId) {
        Payment payment = findByOrderId(orderId);
        payment.fail(FailReason.USER_CANCELLED);
    }

    @Transactional
    public void cancelPayment(Payment payment) {
        payment.cancel();
    }

//    public Map<Long, Payment> findPaymentMapByOrderIds(List<Long> orderIds) {
//        if (orderIds.isEmpty()) return Map.of();
//        return paymentRepository.findByOrderIdIn(orderIds).stream()
//                .collect(Collectors.toMap(p -> p.getOrder().getId(), p -> p));
//    }
}