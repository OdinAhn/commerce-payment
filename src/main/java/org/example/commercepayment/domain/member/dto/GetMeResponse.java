package org.example.commercepayment.domain.member.dto;

import org.example.commercepayment.domain.member.entity.Member;

import java.time.LocalDateTime;

public record GetMeResponse(
        Long id,
        String email,
        String name,
        String phoneNumber,
        int point,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static GetMeResponse from (Member member) {
        return new GetMeResponse(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getPhoneNumber(),
                member.getPoint(),
                member.getCreatedAt(),
                member.getUpdatedAt()
        );
    }
}
