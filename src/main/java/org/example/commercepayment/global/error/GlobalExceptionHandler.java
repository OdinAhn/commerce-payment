package org.example.commercepayment.global.error;

import lombok.extern.slf4j.Slf4j;
import org.example.commercepayment.global.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
        ErrorCode code = e.getErrorCode();
        return ResponseEntity.status(code.getStatus())
                .body(ApiResponse.error(code, e.getMessage()));
    }

}
