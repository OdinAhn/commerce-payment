package org.example.commercepayment.domain.point.service;

import lombok.RequiredArgsConstructor;
import org.example.commercepayment.domain.member.entity.Member;
import org.example.commercepayment.domain.member.repository.MemberRepository;
import org.example.commercepayment.domain.point.dto.PointBalanceResponse;
import org.example.commercepayment.domain.point.dto.PointTransactionResponse;
import org.example.commercepayment.domain.point.repository.PointTransactionRepository;
import org.example.commercepayment.global.error.BusinessException;
import org.example.commercepayment.global.error.ErrorCode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 조회 전용. PointService는 MANDATORY라 스스로 트랜잭션을 못 열기 때문에 분리했다.
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PointQueryService {

    private final MemberRepository memberRepository;
    private final PointTransactionRepository pointTransactionRepository;

    // 잔액 조회. 원장을 SUM하지 않고 스냅샷 컬럼을 읽는다 (거래가 쌓일수록 SUM은 느려짐).
    public PointBalanceResponse getBalance(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        return PointBalanceResponse.from(member);
    }

    // 내 거래 내역 최신순 조회
    public Page<PointTransactionResponse> getTransactions(Long memberId, Pageable pageable) {
        return pointTransactionRepository
                .findByMember_IdOrderByIdDesc(memberId, pageable)
                .map(PointTransactionResponse::from);
    }

    // 정합성 검증용: 잔액 스냅샷 == 원장 합계 인지 확인
    public boolean isBalanceConsistent(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));
        return member.getPointBalance() == pointTransactionRepository.sumAmountByMemberId(memberId);
    }
}
