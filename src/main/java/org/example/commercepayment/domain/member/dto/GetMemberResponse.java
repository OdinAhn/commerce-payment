package org.example.commercepayment.domain.member.dto;

import org.example.commercepayment.domain.member.entity.Member;

import java.time.LocalDateTime;

public record GetMemberResponse(
        Long id,
        String email,
        String name,
        String phoneNumber,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static GetMemberResponse from (Member member) {
        return new GetMemberResponse(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getPhoneNumber(),
                member.getCreatedAt(),
                member.getUpdatedAt()
        );
    }
}
