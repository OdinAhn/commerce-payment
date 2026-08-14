package org.example.commercepayment.domain.member.controller;

import org.example.commercepayment.domain.member.dto.MemberResponse;
import org.example.commercepayment.domain.member.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/me")
    public ResponseEntity<MemberResponse> me(@AuthenticationPrincipal Long memberId) {
        return ResponseEntity.ok(memberService.getMe(memberId));
    }
}
