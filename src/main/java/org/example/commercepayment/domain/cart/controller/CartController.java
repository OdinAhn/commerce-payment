package org.example.commercepayment.domain.cart.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.commercepayment.domain.cart.dto.AddCartRequest;
import org.example.commercepayment.domain.cart.dto.AddCartResponse;
import org.example.commercepayment.domain.cart.dto.CartResponse;
import org.example.commercepayment.domain.cart.dto.UpdateCartRequest;
import org.example.commercepayment.domain.cart.facade.CartFacade;
import org.example.commercepayment.domain.cart.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/carts")            // URL은 팀 규칙에 맞게 /api/carts 로 복수형을 쓰셔도 좋습니다.
@RequiredArgsConstructor

public class CartController {

    private final CartFacade cartFacade;
    private final CartService cartService;

    // 응답 타입 변경 (List -> CartResponse)
    @GetMapping
    public ResponseEntity<CartResponse> getItems(@AuthenticationPrincipal Long memberId) {
        return ResponseEntity.ok(cartService.getCartItems(memberId));
    }

    @PostMapping("/items")
    public ResponseEntity<AddCartResponse> addItem(@AuthenticationPrincipal Long memberId,
                                                   @Valid @RequestBody AddCartRequest request) {
        Long cartItemId = cartFacade.addItem(memberId, request);
        return ResponseEntity.ok(new AddCartResponse(cartItemId));
    }

    @PatchMapping("/items/{id}")
    public ResponseEntity<Void> updateQuantity(@AuthenticationPrincipal Long memberId,
                                               @PathVariable Long id,
                                               @Valid @RequestBody UpdateCartRequest request) {
        cartService.updateQuantity(memberId, id, request.quantity());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Void> removeItem(@AuthenticationPrincipal Long memberId,
                                           @PathVariable Long itemId) {
        cartService.removeItem(memberId, itemId);
        return ResponseEntity.ok().build();
    }

    // 추가: 장바구니 전체 비우기 API
    @DeleteMapping
    public ResponseEntity<Void> clearCart(@AuthenticationPrincipal Long memberId) {
        cartService.clearCart(memberId);
        return ResponseEntity.ok().build();
    }
}