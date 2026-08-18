package org.example.commercepayment.domain.refund.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.commercepayment.domain.refund.dto.RefundRequest;
import org.example.commercepayment.domain.refund.dto.RefundResponse;
import org.example.commercepayment.domain.refund.entity.Refund;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefundFacade {

    private final RefundService refundService;

    /**
     * 트랜잭션 바깥에서 전체 환불 흐름(Orchestration) 제어
     */
    public RefundResponse processRefund(Long memberId, RefundRequest request) {
        
        // Step 1: DB 선검증
        refundService.validateRefundRequest(memberId, request);

        // Step 1: PG사 선검증 (실제 PG사에서 환불 가능한 금액 확인)
        // TODO: PortOne 등 PG사 조회 API 연동

        // [Step 2 ~ 6] 금액 산정, 포인트 회수 및 DB 갱신
        Refund savedRefund = refundService.calculateAndSaveRefund(request);

        boolean isPgSuccess = false;
        try {
            // Step 7: PG 취소 호출
            // TODO: 'PG 환불 금액(savedRefund.getPgRefundAmount())'으로 PortOne 결제 취소 API 호출
            
            // 가상의 통신 성공 처리
            isPgSuccess = true;
            log.info("PG사 환불 통신 성공. Refund ID: {}", savedRefund.getId());
        } catch (Exception e) {
            log.error("PG사 환불 통신 실패. Refund ID: {}, Reason: {}", savedRefund.getId(), e.getMessage());
            isPgSuccess = false;
        }

        // Step 8: 결과 갱신
        refundService.updateRefundResult(savedRefund.getId(), isPgSuccess);

        // 변경된 상태(DB 갱신 결과)를 가져오기 위해 응답 객체 생성
        return RefundResponse.from(savedRefund);
    }
}
