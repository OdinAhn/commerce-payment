package org.example.commercepayment.domain.member.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.commercepayment.global.entity.BaseTimeEntity;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "members")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    protected String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 13)
    private String phoneNumber;

    // point 잔액
    @Column(name = "point_balance", nullable = false)
    private int pointBalance = 0; // 신규가입 시 0P로 시작

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Member(String email, String password, String name, String phoneNumber) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    // 포인트 잔액 변경 메서드
    // Member는 얼마인지만 알고, 왜 바뀌는지는 PointService에서 처리
    public void addPoint(int signedAmount) {
        this.pointBalance += signedAmount;
    }
}
