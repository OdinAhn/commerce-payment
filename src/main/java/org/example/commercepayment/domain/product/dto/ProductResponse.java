package org.example.commercepayment.domain.product.dto;

public record ProductResponse(

        Long id,
        String name,
        int price,
        int stock,
        String description
) {}
