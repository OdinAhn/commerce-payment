package org.example.commercepayment.domain.order.dto;

import java.util.List;

public record OrderCheckoutRequest(List<Long> cartItemIds) {

    public OrderCheckoutRequest {
        if (cartItemIds == null) {
            cartItemIds = List.of();
        }
    }
}
