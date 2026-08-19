package org.example.commercepayment.domain.point.repository;

import org.example.commercepayment.domain.point.entity.PointTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PointTransactionRepository extends JpaRepository<PointTransaction, Long> {

    // 내 거래 내역 최신순
    Page<PointTransaction> findByMember_IdOrderByIdDesc(Long memberId, Pageable pageable);

    // 정합성 검증 전용. 평상시 잔액 조회에 쓰면 안 된다 (거래가 쌓일수록 느려짐).
    // 반환이 long인 이유: SQL SUM()은 BIGINT로 나온다.
    @Query("select coalesce(sum(pt.amount), 0) from PointTransaction pt where pt.member.id = :memberId")
    long sumAmountByMemberId(@Param("memberId") Long memberId);
}
