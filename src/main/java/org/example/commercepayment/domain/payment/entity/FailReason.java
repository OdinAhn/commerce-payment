package org.example.commercepayment.domain.payment.entity;

public enum FailReason {
    PG_DECLINED,
    AMOUNT_MISMATCH,
    USER_CANCELLED
}