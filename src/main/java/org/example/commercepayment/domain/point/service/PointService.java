package org.example.commercepayment.domain.point.service;

import lombok.RequiredArgsConstructor;
import org.example.commercepayment.domain.member.entity.Member;
import org.example.commercepayment.domain.member.repository.MemberRepository;
import org.example.commercepayment.domain.payment.entity.Payment;
import org.example.commercepayment.domain.point.entity.PointTransaction;
import org.example.commercepayment.domain.point.entity.PointTransactionType;
import org.example.commercepayment.domain.point.repository.PointTransactionRepository;
import org.example.commercepayment.global.error.BusinessException;
import org.example.commercepayment.global.error.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.example.commercepayment.domain.point.entity.PointTransactionType.*;

// 포인트 변경 서비스. API가 아니라 결제/환불 도메인이 호출하는 4개 메서드가 핵심이다.
//
// 모든 메서드가 Propagation.MANDATORY인 이유:
// 호출자 트랜잭션에 반드시 참여해야 한다. REQUIRES_NEW로 따로 커밋되면
// 바깥이 롤백됐을 때 "주문은 취소됐는데 포인트만 빠져나간" 상태가 되고 되돌릴 수 없다.
@Service
@RequiredArgsConstructor
public class PointService {

    private final PointTransactionRepository pointTransactionRepository;
    private final MemberRepository memberRepository;

    // 포인트 사용. 4개 중 유일하게 잔액을 검증하며, 부족하면 예외로 호출자 트랜잭션을 롤백시킨다.
    @Transactional(propagation = Propagation.MANDATORY)
    public void use(Long memberId, Payment payment, int amount) {
        Member member = lockMember(memberId);
        if (member.getPointBalance() < amount) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_POINT_BALANCE);
        }
        apply(member, payment, USE, amount);
    }

    // 결제 완료 시 적립. 적립액 산정(PG 결제액의 1%)은 호출자 책임.
    @Transactional(propagation = Propagation.MANDATORY)
    public void accrue(Long memberId, Payment payment, int amount) {
        apply(lockMember(memberId), payment, ACCRUE, amount);
    }

    // 환불 시 사용했던 포인트를 되돌려준다.
    @Transactional(propagation = Propagation.MANDATORY)
    public void restoreUsed(Long memberId, Payment payment, int amount) {
        apply(lockMember(memberId), payment, RESTORE_USED, amount);
    }

    // 환불 시 적립분 회수. 잔액 검증을 하지 않는다 (음수 잔액 허용 정책 — 이후 적립과 상계됨).
    @Transactional(propagation = Propagation.MANDATORY)
    public void reclaim(Long memberId, Payment payment, int amount) {
        apply(lockMember(memberId), payment, RECLAIM, amount);
    }

    // 잔액 갱신과 원장 기록을 한 몸으로 묶는다. 둘 중 하나만 실행되면 정합성이 깨지므로 통로를 하나로 좁혔다.
    private void apply(Member member, Payment payment, PointTransactionType type, int amount) {
        int signed = type.applySign(amount);
        int balanceAfter = member.getPointBalance() + signed;

        member.addPoint(signed);
        pointTransactionRepository.save(
                PointTransaction.record(member, payment, type, amount, balanceAfter)
        );
    }

    // 비관적 락으로 조회. 없으면 동시 요청이 모두 잔액 검증을 통과해 초과 사용된다.
    private Member lockMember(Long memberId) {
        return memberRepository.findByIdForUpdate(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
    }
}
