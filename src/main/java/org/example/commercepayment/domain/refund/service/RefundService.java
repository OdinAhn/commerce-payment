package org.example.commercepayment.domain.refund.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.commercepayment.domain.refund.dto.RefundRequest;
import org.example.commercepayment.domain.refund.dto.RefundRequest.RefundItemRequest;
import org.example.commercepayment.domain.refund.entity.Refund;
import org.example.commercepayment.domain.refund.entity.RefundItem;
import org.example.commercepayment.domain.refund.entity.RefundStatus;
import org.example.commercepayment.domain.refund.repository.RefundItemRepository;
import org.example.commercepayment.domain.refund.repository.RefundRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefundService {

    private final RefundRepository refundRepository;
    private final RefundItemRepository refundItemRepository;

    /**
     * DB 선검증
     */
    @Transactional(readOnly = true)
    public void validateRefundRequest(Long memberId, RefundRequest request) {
        // TODO: 본인 소유 주문 및 결제 검증 (Payment/Order 조회)
        // TODO: 결제 상태(완료/부분환불) 확인

        for (RefundItemRequest item : request.items()) {
            int remainingQuantity = calculateRemainingQuantity(item.orderItemId());
            
            // TODO: 환불 대상 상품의 잔여 환불 가능 수량 초과 여부 확인
            if (item.requestQuantity() > remainingQuantity) {
                throw new IllegalArgumentException("상품 ID " + item.orderItemId() + "의 잔여 환불 가능 수량을 초과했습니다.");
            }
        }
        
        // TODO: 동일 환불에 대한 중복 요청 여부 확인
    }

    /**
     * 금액 산정
     */
    @Transactional
    public Refund calculateAndSaveRefund(RefundRequest request) {
        // 총 환불 요청 수량
        int totalRequestedQuantity = request.items().stream()
                .mapToInt(RefundItemRequest::requestQuantity)
                .sum();

        // TODO: 최초 주문 총 수량
        int totalOriginalPaymentQuantity = 0;

        int totalRefundedPaymentQuantity = refundItemRepository.sumRefundedQuantityByPaymentId(request.paymentId());
        int paymentRemainingQuantity = totalOriginalPaymentQuantity - totalRefundedPaymentQuantity;

        int totalPointRefundAmount = 0;
        int totalPgRefundAmount = 0;

        // [Step 3] 금액 산정 (분기 A/B 판단)
        /* 총 환불 요청 수량 == 주문 전체 잔여 수량 */
        if (totalRequestedQuantity == paymentRemainingQuantity) {
            // 분기 A: 전액 / 마지막 환불
            // TODO: 남은 잔액 1원까지 전부 환불하는 계산식 적용 
            
            // TODO: 결제 상태 ➡️ 전액환불
            // TODO: 주문 상태 ➡️ 주문취소
        } else {
            // 분기 B: 일반 부분 환불
            // TODO: 가격 스냅샷 비례 계산식 적용 
            
            // TODO: 결제 상태 ➡️ 부분환불
            // 주문 상태 ➡️ 주문완료 (유지)
        }

        // [Step 4] 포인트 적립금 회수 산정
        // TODO: 총 환불 금액 비율에 맞춰 포인트 회수액 계산 및 마이너스/정상 차감 처리

        // [Step 5] DB 갱신 (부모 객체 생성)
        Refund refund = Refund.create(
                request.paymentId(),
                request.cancelReason(),
                totalPointRefundAmount,
                totalPgRefundAmount
        );

        // 자식 객체 다중 생성 및 연관관계
        for (RefundItemRequest item : request.items()) {
            // TODO: 각 상품별 개별 비례 환불 금액 계산 로직 필요
            int itemPointRefundAmount = 0; 
            int itemPgRefundAmount = 0;

            RefundItem refundItem = RefundItem.create(
                    item.orderItemId(),
                    item.requestQuantity(),
                    itemPointRefundAmount,
                    itemPgRefundAmount
            );
            
            // 부모 리스트에 쏙 넣음 (Cascade 옵션에 의해 나중에 자동 저장됨)
            refund.addRefundItem(refundItem);

            // TODO: 환불된 상품 재고 수량 원복 (+) (ProductService 연동)
        }

        return refundRepository.save(refund);
    }

    /**
     * 환불 결과 갱신
     */
    @Transactional
    public void updateRefundResult(Long refundId, boolean isPgSuccess) {
        // [Step 8] 결과 갱신
        Refund refund = refundRepository.findById(refundId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 환불 건입니다."));

        if (!isPgSuccess) {
            refund.changeStatus(RefundStatus.FAILED);
            // 수동 보정을 위한 심각(ERROR) 로그 기록
            log.error("[CRITICAL: 수동 보정 요망] PG사 환불 통신 실패! " +
                      "DB 상태(재고, 결제)는 환불 처리되었으나 실제 PG 환불이 누락되었습니다. " +
                      "포트원 관리자 센터에서 수동 취소가 필요합니다. -> Refund ID: {}, Payment ID: {}, PG환불요청액: {}", 
                      refund.getId(), refund.getPaymentId(), refund.getPgRefundAmount());
        }
    }

    /**
     * 특정 주문 상품의 잔여 환불 가능 수량을 계산
     */
    private int calculateRemainingQuantity(Long orderItemId) {
        // 이미 환불이 완료된 누적 수량 조회
        int refundedQuantity = refundItemRepository.sumRefundedQuantityByOrderItemId(orderItemId);
        
        // 최초 주문 수량 조회
        // TODO: OrderItem 연동 시 변경 (임시로 0 처리)
        int originalQuantity = 0;
        
        return originalQuantity - refundedQuantity;
    }
}
