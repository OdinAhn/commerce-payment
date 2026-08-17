package org.example.commercepayment.domain.payment.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.commercepayment.domain.order.entity.Order;
import org.example.commercepayment.global.entity.BaseTimeEntity;
import org.example.commercepayment.global.error.BusinessException;
import org.example.commercepayment.global.error.ErrorCode;

import java.time.LocalDateTime;
import java.util.UUID;

// 결제. 주문 1건당 1건이며, 재시도는 새 주문으로 처리한다.
// 핵심은 금액을 4개로 쪼개 저장하는 것 — 하나로 합치면 부분 환불 때
// "카드로 얼마, 포인트로 얼마" 돌려줄지 계산할 근거가 사라진다.
@Entity
@Table(name = "payments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    // PortOne에 보낼 결제 식별자. 서버가 미리 채번한다(PortOne v2 규격).
    // 결제 확정과 웹훅 모두 이 값을 기준으로 조회하며, DB PK와는 별개 컬럼이다.
    @Column(name = "portone_payment_id", nullable = false, unique = true, length = 200)
    private String portonePaymentId;

    // ── 금액 4분할 ──────────────────────────────

    // 주문 총액 (= pointAmount + pgAmount)
    @Column(name = "total_amount", nullable = false)
    private int totalAmount;

    // 포인트로 결제한 금액
    @Column(name = "point_amount", nullable = false)
    private int pointAmount;

    // 카드(PG)로 실제 결제한 금액. 0이면 PG 호출을 생략한다.
    @Column(name = "pg_amount", nullable = false)
    private int pgAmount;

    // 적립 포인트 스냅샷. 환불 시 회수액 산정의 근거다.
    // 적립률 정책이 바뀌어도 과거 결제는 당시 적립액 기준으로 회수해야 하므로 따로 저장한다.
    @Column(name = "accrued_point", nullable = false)
    private int accruedPoint;

    // ───────────────────────────────────────────

    // 생성 시엔 PG 시도 전 상태
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PaymentStatus status = PaymentStatus.IN_PROGRESS;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;

    // 주문 생성과 같은 트랜잭션에서 호출된다. 결제를 미리 기록해두는 단계.
    public Payment(Order order, int totalAmount, int pointAmount) {
        // 포인트가 총액을 넘으면 pgAmount가 음수가 되므로 여기서 막는다
        if (pointAmount < 0 || pointAmount > totalAmount) {
            throw new BusinessException(ErrorCode.INVALID_INPUT);
        }
        this.order = order;
        this.totalAmount = totalAmount;
        this.pointAmount = pointAmount;
        this.pgAmount = totalAmount - pointAmount;
        this.accruedPoint = 0;                       // 적립액은 결제 확정 때 확정된다
        this.portonePaymentId = generatePortonePaymentId();
    }

    // 결제 확정. 상태 전이 + 적립액 기록 + 완료 일시를 한 번에 처리한다.
    // 웹훅과 클라이언트 확정이 동시에 와도 transitTo에서 두 번째가 막혀 이중 적립이 없다.
    public void markPaid(int accruedPoint) {
        transitTo(PaymentStatus.PAID);
        this.accruedPoint = accruedPoint;
        this.paidAt = LocalDateTime.now();
    }

    // 결제 실패·회원 취소. 결제 완료 전에 끝나는 모든 경우가 여기로 모인다.
    public void markFailed() {
        transitTo(PaymentStatus.FAILED);
    }

    // 상태 변경의 유일한 통로 (세터를 만들지 않는 이유)
    public void transitTo(PaymentStatus target) {
        if (!this.status.canTransitTo(target)) {
            throw new BusinessException(ErrorCode.INVALID_PAYMENT_STATUS);
        }
        this.status = target;
    }

    // PG 호출 없이 포인트만으로 끝나는 결제인지
    public boolean isPointOnly() {
        return this.pgAmount == 0;
    }

    private static String generatePortonePaymentId() {
        return "pay_" + UUID.randomUUID();
    }
}
