package org.example.commercepayment.domain.product.dto;

import java.util.List;

// totalCount와 현재 페이지 메타를 포함하기 위해 응답 전용 DTO 생성

public record ProductPageResponse(
        List<ProductResponse> data,
        PageInfo pageInfo
) {
    public record PageInfo(
            int currentPage,
            int size,
            long totalElements,
            int totalPages
    ) {}
}