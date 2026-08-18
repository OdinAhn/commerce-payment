package org.example.commercepayment.domain.member.controller;

import lombok.RequiredArgsConstructor;
import org.example.commercepayment.domain.member.dto.GetMeResponse;
import org.example.commercepayment.domain.member.service.MemberService;
import org.example.commercepayment.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;


    @GetMapping("/me")
    public ResponseEntity<ApiResponse<GetMeResponse>> getMe() {

        // TODO : 인증 관련 함수에서 내 id 가져오기

        GetMeResponse response = memberService.getOne(0L);

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
