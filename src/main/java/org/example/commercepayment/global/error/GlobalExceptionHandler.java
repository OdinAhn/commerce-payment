package org.example.commercepayment.global.error;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.commercepayment.global.response.ApiResponse;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

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
    // Arg 에러 핸들
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("IllegalArgumentException: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(HttpStatus.BAD_REQUEST.value(), ErrorCode.INVALID_INPUT_VALUE.getCode(), e.getMessage()));
    }

    // State 에러 핸들
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalStateException(IllegalStateException e) {
        log.warn("IllegalStateException: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(HttpStatus.BAD_REQUEST.value(), ErrorCode.INVALID_INPUT_VALUE.getCode(), e.getMessage()));
    }

    // 커스텀 에러 핸들
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<?> handleCustomException(CustomException ex, HttpServletRequest request) {
        log.warn("CustomException: code={}, message={}", ex.getErrorCode(), ex.getMessage());

        String uri = request.getRequestURI();
        String accept = request.getHeader(HttpHeaders.ACCEPT);

        boolean isHtmlRequest = (uri != null && (uri.startsWith("/view/") || uri.equals("/")))
                || (accept != null && accept.contains("text/html"));

        if (isHtmlRequest) {
            if (ex.getStatus() == HttpStatus.UNAUTHORIZED) {
                return ResponseEntity.status(HttpStatus.FOUND)
                        .header(HttpHeaders.LOCATION, "/view/login")
                        .build();
            } else if (ex.getStatus() == HttpStatus.FORBIDDEN) {
                return ResponseEntity.status(HttpStatus.FOUND)
                        .header(HttpHeaders.LOCATION, "/view/error/403")
                        .build();
            }
        }

        return ResponseEntity
                .status(ex.getStatus())
                .body(ApiResponse.fail(ex.getStatus().value(), ex.getErrorCode(), ex.getMessage()));
    }

    // Valid 에러 핸들 (DTO 검증 실패)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .orElse("입력 값이 올바르지 않습니다.");

        log.warn("Validation Error: {}", errorMessage);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(HttpStatus.BAD_REQUEST.value(), ErrorCode.INVALID_INPUT_VALUE.getCode(), errorMessage));
    }

    // 404 Not Found
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<?> handleNoResourceFoundException(NoResourceFoundException ex, HttpServletRequest request) {
        String uri = request.getRequestURI();
        String accept = request.getHeader(HttpHeaders.ACCEPT);

        boolean isHtmlRequest = (uri != null && (uri.startsWith("/view/") || uri.equals("/")))
                || (accept != null && accept.contains("text/html"));

        if (isHtmlRequest) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION, "/view/error/404")
                    .build();
        }

        log.error("404 Not Found: {}", uri);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.fail(HttpStatus.NOT_FOUND.value(), "C005", "요청한 리소스를 찾을 수 없습니다."));
    }

    // 알 수 없는 서버 에러 핸들
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception ex) {
        log.error("Unhandled Exception 발생!", ex);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail(HttpStatus.INTERNAL_SERVER_ERROR.value(), ErrorCode.INTERNAL_SERVER_ERROR.getCode(), ErrorCode.INTERNAL_SERVER_ERROR.getMessage()));
    }
}
