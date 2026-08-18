package org.example.commercepayment.domain.auth.service;


import lombok.RequiredArgsConstructor;
import org.example.commercepayment.domain.member.dto.AuthResponse;
import org.example.commercepayment.domain.member.dto.LoginRequest;
import org.example.commercepayment.domain.member.dto.SignupRequest;
import org.example.commercepayment.domain.member.entity.Member;
import org.example.commercepayment.domain.member.repository.MemberRepository;
import org.example.commercepayment.global.jwt.JwtProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    @Transactional
    public void signup(SignupRequest request) {
        if (memberRepository.existsByEmail(request.email())) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }
        Member member = new Member(
                request.email(),
                passwordEncoder.encode(request.password()),
                request.name(),
                request.phoneNumber()
        );
        memberRepository.save(member);
    }

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
