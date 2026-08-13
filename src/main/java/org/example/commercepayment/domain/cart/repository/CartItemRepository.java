package org.example.commercepayment.domain.cart.repository;

import org.example.commercepayment.domain.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @Query("SELECT ci FROM CartItem ci JOIN FETCH ci.product WHERE ci.member.id = :memberId")
    List<CartItem> findByMemberId(@Param("memberId") Long memberId);

    Optional<CartItem> findByMember_IdAndProduct_Id(Long memberId, Long productId);

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.id = :id AND ci.member.id = :memberId")
    int deleteByIdAndMember_Id(@Param("id") Long id, @Param("memberId") Long memberId);

    // 장바구니 전체 비우기 기능을 위해 memberId로 모든 항목을 지우는 메서드 추가
    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.member.id = :memberId")
    void deleteAllByMember_Id(@Param("memberId") Long memberId);

}
