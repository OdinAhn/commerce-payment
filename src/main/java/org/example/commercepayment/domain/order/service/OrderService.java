package org.example.commercepayment.domain.order.service;

import lombok.RequiredArgsConstructor;
import org.example.commercepayment.domain.member.entity.Member;
import org.example.commercepayment.domain.order.dto.OrderItemResponse;
import org.example.commercepayment.domain.order.dto.OrderResponse;
import org.example.commercepayment.domain.order.entity.Order;
import org.example.commercepayment.domain.order.entity.OrderItem;
import org.example.commercepayment.domain.order.repository.OrderRepository;
import org.example.commercepayment.global.error.BusinessException;
import org.example.commercepayment.global.error.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final OrderRepository orderRepository;

    // 주문 생성
    @Transactional
    public Order createOrder(Member member, List<OrderItem> orderItems, int totalPrice) {
        Order order = new Order(member, totalPrice, orderItems);
        return orderRepository.save(order);
    }

    // 내 주문 목록 조회 (최신순)
    public List<Order> findOrderEntities(Long memberId) {
        return orderRepository.findByMemberIdOrderByCreatedAtDesc(memberId);
    }

    // 주문 단건 상세 조회 : orderId만으로 조회
    public Order findOrderEntity(Long orderId) {
        return orderRepository.findByIdWithOrderItems(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
    }

    // Order -> OrderResponse 변환, OrderItem -> OrderItemResponse 변환
    public OrderResponse toResponse(Order order, Long paymentId) {
        List<OrderItemResponse> items = order.getOrderItems().stream()
                .map(oi -> new OrderItemResponse(oi.getProductName(), oi.getOrderPrice(), oi.getQuantity()))
                .toList();
        return new OrderResponse(
                order.getId(),
                paymentId,
                order.getTotalPrice(),
                order.getStatus().name(),
                order.getOrderName(),
                order.getCreatedAt(),
                items
        );
    }

}

