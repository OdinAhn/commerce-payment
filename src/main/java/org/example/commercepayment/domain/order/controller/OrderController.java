package org.example.commercepayment.domain.order.controller;

import lombok.RequiredArgsConstructor;
import org.example.commercepayment.domain.order.dto.CheckoutResponse;
import org.example.commercepayment.domain.order.dto.OrderCheckoutRequest;
import org.example.commercepayment.domain.order.dto.OrderCheckoutResponse;
import org.example.commercepayment.domain.order.dto.OrderResponse;
import org.example.commercepayment.domain.order.facade.OrderFacade;
import org.example.commercepayment.global.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderFacade orderFacade;

    @GetMapping("/checkout")
    public ResponseEntity<ApiResponse<CheckoutResponse>> checkout(
            @AuthenticationPrincipal Long memberId,
            @RequestParam(required = false) List<Long> cartItemIds
    ) {
        return ResponseEntity.ok(ApiResponse.ok(orderFacade.getCheckout(memberId, cartItemIds)));
    }

    @PostMapping("/checkout")
    public ResponseEntity<ApiResponse<OrderCheckoutResponse>> createOrder(
            @AuthenticationPrincipal Long memberId,
            @RequestBody(required = false) OrderCheckoutRequest request) {
        OrderCheckoutResponse response = orderFacade.createOrder(memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.ok(response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponse>>> getOrders(@AuthenticationPrincipal Long memberId) {
        return ResponseEntity.ok(ApiResponse.ok(orderFacade.getOrders(memberId)));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse<OrderResponse>> getOrder(@AuthenticationPrincipal Long memberId,
                                                               @PathVariable Long orderId) {
        return ResponseEntity.ok(ApiResponse.ok(orderFacade.getOrder(memberId, orderId)));
    }

    //결제대기 상태인 주문 취소
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<ApiResponse<Void>> cancelOrder(@AuthenticationPrincipal Long memberId,
                                                         @PathVariable Long orderId) {
        orderFacade.cancelOrder(memberId, orderId);

        return ResponseEntity.ok(ApiResponse.ok(null));
    }

}

