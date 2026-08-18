package org.example.commercepayment.domain.auth.service;

import lombok.RequiredArgsConstructor;
import org.example.commercepayment.domain.auth.dto.AuthResponse;
import org.example.commercepayment.domain.auth.dto.LoginRequest;
import org.example.commercepayment.domain.auth.dto.SignupRequest;
import org.example.commercepayment.domain.member.entity.Member;
import org.example.commercepayment.domain.member.repository.MemberRepository;
import org.example.commercepayment.global.error.CustomException;
import org.example.commercepayment.global.error.ErrorCode;
import org.example.commercepayment.global.jwt.JwtProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    // 회원가입
    @Transactional
    public void signup(SignupRequest request) {

        validateDuplicateEmail(request.email());

        String encodePassword = passwordEncoder.encode(request.password());

        Member member = new Member(
                request.email(),
                encodePassword,
                request.name(),
                request.phoneNumber()
        );

        memberRepository.save(member);
    }

    private void validateDuplicateEmail(String email) {
        if(memberRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.DUPLICATED_EMAIL);
        }
    }

    // 로그인
    public AuthResponse login(LoginRequest request) {
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("이메일 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new RuntimeException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }
        String token = jwtProvider.createToken(member.getId(), member.getEmail());
        return new AuthResponse(token, toMemberInfo(member));
    }

    private AuthResponse.MemberInfo toMemberInfo(Member member) {
        return new AuthResponse.MemberInfo(member.getId(), member.getName(), member.getEmail(), member.getPhoneNumber());
    }

}
