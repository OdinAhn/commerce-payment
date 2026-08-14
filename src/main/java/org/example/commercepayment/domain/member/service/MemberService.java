package org.example.commercepayment.domain.member.service;

import lombok.RequiredArgsConstructor;
import org.example.commercepayment.domain.member.dto.GetMeResponse;
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

    // 내 정보 보기
    @Transactional(readOnly = true)
    public GetMeResponse getOne (Long id) {

        Member member = memberRepository.findById(id).orElseThrow(
                ()-> new CustomException(ErrorCode.NOT_FOUND_MEMBER));

        return GetMeResponse.from(member);
    }


}
