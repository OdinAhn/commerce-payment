package org.example.commercepayment.domain.cart.repository;

import org.example.commercepayment.domain.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    @Query("SELECT ci FROM CartItem ci JOIN FETCH ci.product WHERE ci.cart.member.id = :memberId")
    List<CartItem> findByMemberId(@Param("memberId") Long memberId);

    // Spring Data JPA 명명 규칙에 따라 경로 변경
    Optional<CartItem> findByCart_Member_IdAndProduct_Id(Long memberId, Long productId);

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.id = :id AND ci.cart.member.id = :memberId")
    int deleteByIdAndMember_Id(@Param("id") Long id, @Param("memberId") Long memberId);

    @Modifying
    @Query("DELETE FROM CartItem ci WHERE ci.cart.member.id = :memberId")
    void deleteAllByMember_Id(@Param("memberId") Long memberId);

    @Query("SELECT ci FROM CartItem ci JOIN FETCH ci.product WHERE ci.id IN :ids AND ci.cart.member.id = :memberId")
    List<CartItem> findByIdInAndMember_IdWithProduct(@Param("ids") List<Long> ids, @Param("memberId") Long memberId);

    @Modifying
    @Query("DELETE FROM CartItem c WHERE c.id IN :ids AND c.cart.member.id = :memberId")
    int deleteAllByIdInAndMemberId(@Param("ids") List<Long> ids, @Param("memberId") Long memberId);

}
