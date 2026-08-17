package org.example.commercepayment.domain.point.dto;

import org.example.commercepayment.domain.member.entity.Member;

// 포인트 잔액 응답. 환불로 적립분이 회수되면 음수가 될 수 있다.
public record PointBalanceResponse(int balance) {

    public static PointBalanceResponse from(Member member) {
        return new PointBalanceResponse(member.getPointBalance());
    }
}
