package org.example.commercepayment.domain.payment.service;

import lombok.RequiredArgsConstructor;
import org.example.commercepayment.domain.order.entity.Order;
import org.example.commercepayment.domain.payment.entity.Payment;
import org.example.commercepayment.domain.payment.repository.PaymentRepository;
import org.example.commercepayment.global.error.BusinessException;
import org.example.commercepayment.global.error.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {

    private final PaymentRepository paymentRepository;

    /**
     * 주문 생성과 동시에 호출된다(단일 트랜잭션 ①).
     * PG 시도 전 '대기' 상태로 사전 기록하고 portonePaymentId를 채번한다.
     */
    @Transactional
    public Payment createPayment(Order order, int totalAmount, int pointAmount) {
        return paymentRepository.save(new Payment(order, totalAmount, pointAmount));
    }

    /** 주문 상세 조회용 — 결제 상태·적립액·PG 금액이 모두 필요해 엔티티 전체를 가져온다. */
    public Payment findByOrderId(Long orderId) {
        return paymentRepository.findByOrder_Id(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));
    }

    /** 주문 목록 조회용 — IN 쿼리 한 번으로 모아 N+1을 피한다. */
    public Map<Long, Payment> findPaymentMapByOrderIds(List<Long> orderIds) {
        if (orderIds.isEmpty()) return Map.of();
        return paymentRepository.findAllByOrder_IdIn(orderIds).stream()
                .collect(Collectors.toMap(p -> p.getOrder().getId(), Function.identity()));
    }

    /** 주문 취소 시 결제도 함께 실패 처리. 상태 전이 규칙은 Payment가 판단한다. */
    @Transactional
    public void markFailed(Long orderId) {
        findByOrderId(orderId).markFailed();
    }
}