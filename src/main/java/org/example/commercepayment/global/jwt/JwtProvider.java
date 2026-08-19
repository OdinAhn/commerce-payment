package org.example.commercepayment.global.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
public class JwtProvider {

    private final SecretKey key;
    private final long expiration;
    private final JwtParser parser;

    public JwtProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration) {
        this.key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
        this.expiration = expiration;
        this.parser = Jwts.parser()
                .verifyWith(this.key)
                .build();
    }

    // memberId,email 클레임을 담아 만료시간/서명을 포함한 JWT를 생성
    public String createToken(Long memberId, String email) {
        Date now = new Date();
        return Jwts.builder()
                .subject(memberId.toString())
                .claim("email", email)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiration))
                .signWith(key)
                .compact();
    }

    // 토큰의 subject(회원 ID)를 파싱해 반환
    public Long getMemberId(String token) {
        return Long.parseLong(parseClaims(token).getSubject());
    }

    // 서명·만료 등 검증을 시도해 성공 여부를 boolean으로 반환
    public boolean validate(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("JWT 검증 실패: {}", e.getMessage());
            return false;
        }
    }

    // 검증 키로 서명된 클레임을 파싱해 Claims를 돌려주는 공통 파서
    private Claims parseClaims(String token) {
        return parser
                .parseSignedClaims(token)
                .getPayload();
    }


}
