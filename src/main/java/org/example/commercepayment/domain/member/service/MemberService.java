package org.example.commercepayment.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.example.commercepayment.domain.member.dto.GetMemberResponse;
import org.example.commercepayment.domain.member.entity.Member;
import org.example.commercepayment.domain.member.repository.MemberRepository;
import org.example.commercepayment.global.error.CustomException;
import org.example.commercepayment.global.error.ErrorCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    // 회원 1명 찾기 (내 회원 정보보기 포함)
    @Transactional(readOnly = true)
    public GetMemberResponse getOne (Long id) {

        Member member = memberRepository.findById(id).orElseThrow(
                ()-> new CustomException(ErrorCode.NOT_FOUND_MEMBER));

        return GetMemberResponse.from(member);
    }


}
