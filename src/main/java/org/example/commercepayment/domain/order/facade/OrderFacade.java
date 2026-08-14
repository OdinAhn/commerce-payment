package org.example.commercepayment.domain.order.facade;

import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.example.commercepayment.domain.cart.entity.CartItem;
import org.example.commercepayment.domain.cart.service.CartService;
import org.example.commercepayment.domain.member.entity.Member;
import org.example.commercepayment.domain.member.service.MemberService;
import org.example.commercepayment.domain.order.dto.*;
import org.example.commercepayment.domain.order.entity.Order;
import org.example.commercepayment.domain.order.entity.OrderItem;
import org.example.commercepayment.domain.order.service.OrderService;
import org.example.commercepayment.domain.payment.entity.Payment;
import org.example.commercepayment.domain.payment.service.PaymentService;
import org.example.commercepayment.domain.product.entity.Product;
import org.example.commercepayment.global.error.BusinessException;
import org.example.commercepayment.global.error.ErrorCode;
import org.hibernate.event.spi.PreInsertEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)

public class OrderFacade {

    private final CartService cartService;
    private final MemberService memberService;
    private final OrderService orderService;
    private final PaymentService paymentService;

    public CheckoutResponse getCheckout(Long memberId, List<Long> cartItemIds) {
        // 주문서 미리보기: 재고 차감/주문 생성 없는 읽기 전용
        // cartItemIds가 null/비어있으면 "전체 장바구니", 값이 있으면 "선택도니 아이템만"
        List<CartItem> cartItems = getValidateCartItems(
                memberId, cartItemIds != null ? cartItemIds : List.of());

        List<CheckoutResponse.CheckoutItemResponse> items = cartItems.stream()
                .map(cartItem -> {
                    int price = cartItem.getProduct().getPrice();
                    int subtotal = price * cartItem.getQuantity();
                    return new CheckoutResponse.CheckoutItemResponse(
                            cartItem.getProductId(),
                            cartItem.getProduct().getName(),
                            price,
                            cartItem.getQuantity(),
                            subtotal);
                })
                .toList();

        // 장바구니 총액
        int totalPrice = items.stream()
                .mapToInt(CheckoutResponse.CheckoutItemResponse::subtotal)
                .sum();

        return new CheckoutResponse(items, totalPrice);
    }
    @Transactional
    public OrderCheckoutResponse createOrder(Long memberId, OrderCheckoutRequest request) {
        List<Long> cartItemIds = (request != null) ? request.cartItemIds() : List.of();

        // 0. 회원 조회
        Member member = memberService.findById(memberId);

        // 1. 장바구니 조회 (선택된 아이템만)
        List<CartItem> cartItems = getValidateCartItems(memberId, cartItemIds);

        // 2~3. 재고 차감 + 스냅샷 OrderItem 생성
        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            product.deductStock(cartItem.getQuantity());

            OrderItem orderItem = new OrderItem(
                    product,
                    product.getPrice(),
                    cartItem.getQuantity()
            );
            orderItems.add(orderItem);
        }
        int totalPrice = orderItems.stream().mapToInt(OrderItem::getSubtotal).sum();

        // 4. 주문 저장
        Order order = orderService.createOrder(member, orderItems, totalPrice);

        // 5. 결제 정보 생성 (IN_PROGRESS 상태)
        Payment payment = paymentService.createPayment(order, order.getTotalPrice());

        // 6. 주문한 장바구니 아이템만 삭제
        List<Long> orderedItemIds = cartItems.stream().map(CartItem::getId).toList();
        cartService.clearCartItems(orderedItemIds, memberId);

        // 7. 응답
        return new OrderCheckoutResponse(
                order.getId(),
                payment.getPortonePaymentId(),
                totalPrice,
                order.getOrderName(),
                order.getStatus().name()
        );
    }

    public List<OrderResponse> getOrders(Long memberId) {
        List<Order> orders = orderService.findOrderEntities(memberId);
        List<Long> orderIds = orders.stream().map(Order::getId).toList();
        Map<Long, Long> paymentMap = paymentService.findPaymentIdMapByOrderIds(orderIds);

        return orders.stream()
                .map(order -> orderService.toResponse(order, paymentMap.get(order.getId())))
                .toList();
    }

    public OrderResponse getOrder(Long memberId, Long orderId) {
        Order order = orderService.findOrderEntity(orderId);
        if (!order.getMemberId().equals(memberId)) {
            throw new BusinessException(ErrorCode.ORDER_NOT_FOUND);
        }
        Long paymentId = paymentService.findPaymentIdByOrderId(orderId).orElseThrow(
                () -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND)
        );

        return orderService.toResponse(order, paymentId);
    }

    // cartItem 리스트가
    private List<CartItem> getValidateCartItems(Long memberId, List<Long> cartItemIds) {

        List<CartItem> cartItems = cartItemIds.isEmpty()
                ? cartService.findCartEntities(memberId)
                : cartService.findCartEntitiesByIds(memberId, cartItemIds);

        // 1차 검증: 장바구니에 아무것도 없다?
        if (cartItems.isEmpty()) {
            throw new BusinessException(ErrorCode.CART_EMPTY);
        }

        // 2차 검증: 선택한 상품 개수가 다르다?(일부가 잘못된 조회)
        if (!cartItemIds.isEmpty() && cartItems.size() != cartItemIds.size()) {
            throw new BusinessException(ErrorCode.CART_ITEM_NOT_FOUND);
        }

        return cartItems;
    }
}
