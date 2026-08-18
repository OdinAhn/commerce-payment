package org.example.commercepayment.domain.auth.dto;

import org.example.commercepayment.domain.member.entity.Member;

public record AuthResponse(
        String token,
        MemberInfo member
) {
    public record MemberInfo(
            Long id,
            String name,
            String email,
            String phoneNumber
    ) {}

    public static AuthResponse of (String token, Member member) {
        return  new AuthResponse(token, new MemberInfo(
                member.getId(),
                member.getName(),
                member.getEmail(),
                member.getPhoneNumber()));
    }
}
