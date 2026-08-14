package org.example.commercepayment.domain.member.controller;

import lombok.RequiredArgsConstructor;
import org.example.commercepayment.domain.member.dto.GetMemberResponse;
import org.example.commercepayment.domain.member.service.MemberService;
import org.example.commercepayment.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MemberController {

    private final MemberService memberService;


    @GetMapping("/me")
    public ResponseEntity<ApiResponse<GetMemberResponse>> getMe() {

        // TODO : 인증 관련 함수에서 내 id 가져오기

        GetMemberResponse result = memberService.getOne(0L);

        return ResponseEntity.ok(ApiResponse.success(result));
    }
}
