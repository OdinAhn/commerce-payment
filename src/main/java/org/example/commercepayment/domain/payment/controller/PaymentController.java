package org.example.commercepayment.domain.payment.controller;

import org.example.commercepayment.domain.payment.dto.PaymentConfirmRequest;
import org.example.commercepayment.domain.payment.dto.PaymentConfirmResponse;
import org.example.commercepayment.domain.payment.entity.FailReason;
import org.example.commercepayment.domain.payment.entity.Payment;
import org.example.commercepayment.domain.payment.facade.PaymentFacade;
import org.example.commercepayment.domain.payment.service.PaymentService;
import org.example.commercepayment.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentFacade paymentFacade;
    private final PaymentService paymentService;

    @PostMapping("/confirm")
    public ApiResponse<PaymentConfirmResponse> confirm(
            @RequestAttribute Long memberId,   // JWT 필터가 채워주는 값이라고 가정
            @Valid @RequestBody PaymentConfirmRequest request
    ) {
        return ApiResponse.ok(paymentFacade.confirmPayment(memberId, request));
    }

    @PostMapping("/{id}/cancel")
    public ApiResponse<Void> cancel(@PathVariable Long id) {
        Payment payment = paymentService.findByIdWithOrder(id);
        if (payment.getPgAmount() == 0 && payment.getAmount() == 0) {
            // no-op guard, 실제로는 status 검증
        }
        paymentService.cancelPayment(payment);
        return ApiResponse.ok(null);
    }

    @PostMapping("/{id}/fail")
    public ApiResponse<Void> fail(
            @PathVariable Long id,
            @RequestBody FailRequest request
    ) {
        Payment payment = paymentService.findByIdWithOrder(id);
        paymentService.failPayment(payment, request.failReason());
        return ApiResponse.ok(null);
    }

    public record FailRequest(FailReason failReason) {}
}