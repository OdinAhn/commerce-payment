package org.example.commercepayment.domain.point.controller;

import lombok.RequiredArgsConstructor;
import org.example.commercepayment.domain.point.dto.PointBalanceResponse;
import org.example.commercepayment.domain.point.dto.PointTransactionResponse;
import org.example.commercepayment.domain.point.service.PointQueryService;
import org.example.commercepayment.global.response.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 포인트 조회 API.
// 회원 ID를 URL로 받지 않고 토큰에서 꺼내 쓴다 — 그래야 "타인 조회" 자체가 불가능해진다.
@RestController
@RequestMapping("/api/points")
@RequiredArgsConstructor
public class PointController {

    private final PointQueryService pointQueryService;

    // 내 포인트 잔액
    @GetMapping("/balance")
    public ResponseEntity<ApiResponse<PointBalanceResponse>> getBalance(
            @AuthenticationPrincipal Long memberId
    ) {
        return ResponseEntity.ok(ApiResponse.ok(pointQueryService.getBalance(memberId)));
    }

    // 내 포인트 거래 내역 (최신순)
    @GetMapping("/transactions")
    public ResponseEntity<ApiResponse<Page<PointTransactionResponse>>> getTransactions(
            @AuthenticationPrincipal Long memberId,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(ApiResponse.ok(pointQueryService.getTransactions(memberId, pageable)));
    }
}
