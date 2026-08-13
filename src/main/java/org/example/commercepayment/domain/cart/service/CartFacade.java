package org.example.commercepayment.domain.cart.service;

import lombok.RequiredArgsConstructor;
import org.example.commercepayment.domain.cart.dto.AddCartRequest;
import org.example.commercepayment.domain.cart.entity.CartItem;
import org.example.commercepayment.domain.product.entity.Product;
import org.example.commercepayment.domain.product.service.ProductService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Member;

@Component
@RequiredArgsConstructor
public class CartFacade {

    private final CartService cartService;
    private final MemberService memberService;
    private final ProductService productService;

    @Transactional
    public Long addItem(Long memberId, AddCartRequest request) {
        Member member = memberService.findById(memberId);
        Product product = productService.findProductEntity(request.productId());
        CartItem cartItem = new CartItem(member, product, request.quantity());
        return cartService.addItem(cartItem);
    }

}