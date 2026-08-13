package org.example.commercepayment.domain.cart.service;

import lombok.RequiredArgsConstructor;
import org.example.commercepayment.domain.cart.dto.CartItemResponse;
import org.example.commercepayment.domain.cart.dto.CartResponse;
import org.example.commercepayment.domain.cart.entity.CartItem;
import org.example.commercepayment.domain.cart.repository.CartItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartService {

    private final CartItemRepository cartItemRepository;

    // fix: 단순 List가 아닌 합계 금액이 포함 된 CartResponse 반환
    public CartResponse getCartItems(Long memberId) {
        List<CartItemResponse> items = cartItemRepository.findByMemberId(memberId).stream()
                .map(this::toResponse)
                .toList();

        // 전체 합계 금액 계산
        int totalPrice = items.stream()
                .mapToInt(CartItemResponse::itemTotalPrice)
                .sum();

        return new CartResponse(totalPrice, items);
    }

    @Transactional
    public Long addItem(CartItem cartItem) {
        Optional<CartItem> existing = cartItemRepository.findByMember_IdAndProduct_Id(
                cartItem.getMemberId(), cartItem.getProductId()
        );

        if (existing.isPresent()) {
            CartItem found = existing.get();
            // 재고 검증: 기존 수량 + 추가할 수량이 재고를 넘는지 확인
            if (found.getProduct().getStock() < found.getQuantity() + cartItem.getQuantity()) {
                throw new IllegalArgumentException("요청 수량이 재고를 초과했습니다.");
            }
            found.addQuantity(cartItem.getQuantity());
            return found.getId();
        } else {
            // 재고 검증: 처음 담을 때 요청 수량이 재고를 넘는지 확인
            if (cartItem.getProduct().getStock() < cartItem.getQuantity()) {
                throw new IllegalArgumentException("요청 수량이 재고를 초과했습니다.");
            }
            return cartItemRepository.save(cartItem).getId();
        }
    }

    @Transactional
    public void updateQuantity(Long memberId, Long itemId, int quantity) {
        CartItem item = cartItemRepository.findById(itemId)
                .filter(ci -> ci.getMemberId().equals(memberId))
                .orElseThrow(() -> new RuntimeException("장바구니 항목을 찾을 수 없습니다."));

        // 재고 검증: 변경하려는 수량이 재고를 넘는지 확인
        if (item.getProduct().getStock() < quantity) {
            throw new IllegalArgumentException("요청 수량이 재고를 초과했습니다.");
        }
        item.changeQuantity(quantity);
    }

    // 추가: 장바구니 전체 비우기
    @Transactional
    public void clearCart(Long memberId) {
        cartItemRepository.deleteAllByMember_Id(memberId);
    }

    private CartItemResponse toResponse(CartItem item) {
        return new CartItemResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getProduct().getPrice(),
                item.getQuantity(),
                item.getProduct().getStock(),
                item.getProduct().getPrice() * item.getQuantity() // 개별 합계 금액 계산
        );
    }
}