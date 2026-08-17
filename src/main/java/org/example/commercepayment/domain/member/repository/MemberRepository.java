package org.example.commercepayment.domain.member.repository;

import jakarta.persistence.LockModeType;
import org.example.commercepayment.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByEmail(String email);
    boolean existsByEmail(String email);

    // 포인트 잔액 검증,갱신 조회. 비관적 쓰기 락
    // 락이 없으면 동시에 여러 주문 건에 보내면 값이 초과되어 통과되는걸 방지
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select m from Member m where m.id = :memberId")
    Optional<Member> findByIdForUpdate(@Param("memberId") Long memberId);
}

