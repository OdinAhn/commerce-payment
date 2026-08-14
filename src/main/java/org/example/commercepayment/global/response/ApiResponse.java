package org.example.commercepayment.global.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    
    private final boolean success;
    private final int code;
    private final String message;
    private final String errorCode;
    private final T data;

    // 성공 응답 (데이터 포함)
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, 200, "요청에 성공하였습니다.", null, data);
    }
    
    // 성공 응답 (메시지 + 데이터 포함)
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, 200, message, null, data);
    }

    // 성공 응답 (데이터 없음)
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(true, 200, "요청에 성공하였습니다.", null, null);
    }

    // 실패 응답
    public static <T> ApiResponse<T> fail(int code, String errorCode, String message) {
        return new ApiResponse<>(false, code, message, errorCode, null);
    }
}
