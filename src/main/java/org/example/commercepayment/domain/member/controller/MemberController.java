package org.example.commercepayment.domain.member.controller;

import lombok.RequiredArgsConstructor;
import org.example.commercepayment.domain.member.dto.GetMeResponse;
import org.example.commercepayment.domain.member.service.MemberService;
import org.example.commercepayment.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;


    @GetMapping("/me")
    public ResponseEntity<ApiResponse<GetMeResponse>> me(
            // 필터에서 SecurityContextHolder.getContext().setAuthentication(...)로 인증 객체를 넣어두면,
            // 컨트롤러에서 @AuthenticationPrincipal로 Authentication#getPrincipal() 값을 바로 받을 수 있다.
            @AuthenticationPrincipal Long memberId
    ) {
        GetMeResponse response = memberService.getOne(memberId);

        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
