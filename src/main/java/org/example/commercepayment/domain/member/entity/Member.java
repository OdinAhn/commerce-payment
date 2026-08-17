package org.example.commercepayment.domain.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.commercepayment.global.entity.BaseTimeEntity;

@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;

    // point 잔액
    @Column(name = "point_balance", nullable = false)
    private int pointBalance = 0; // 신규가입 시 0P로 시작

    public Member(String name, String email, String passwordHash, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.phoneNumber = phoneNumber;
    }

    // 포인트 잔액 변경 메서드
    // Member는 얼마인지만 알고, 왜 바뀌는지는 PointService에서 처리
    public void addPoint(int signedAmount) {
        this.pointBalance += signedAmount;
    }
}

