package org.example.commercepayment.global.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomException extends RuntimeException {

    private final HttpStatus status;
    private final String errorCode;

    public CustomException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.errorCode = "CUSTOM_ERR";
    }

    public CustomException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.status = errorCode.getStatus();
        this.errorCode = errorCode.getCode();
    }

    public CustomException(ErrorCode errorCode, String detailMessage) {
        super(detailMessage);
        this.status = errorCode.getStatus();
        this.errorCode = errorCode.getCode();
    }
}