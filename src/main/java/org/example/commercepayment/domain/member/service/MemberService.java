package org.example.commercepayment.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.example.commercepayment.domain.member.dto.MemberResponse;
import org.example.commercepayment.domain.member.entity.Member;
import org.example.commercepayment.domain.member.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberResponse getMe(Long memberId) {
        Member member = findById(memberId);
        return new MemberResponse(
                member.getId(),
                member.getName(),
                member.getEmail(),
                member.getPhoneNumber(),
                member.getCreatedAt()
        );
    }

    public Member findById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));
    }
}
