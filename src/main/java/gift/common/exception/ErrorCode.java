package gift.common.exception;

import org.springframework.http.HttpStatus;

import java.util.Arrays;

import static gift.common.validation.ValidationMessages.*;

/**
 * 에러의 원인과 유형을 나타냄
 * 각 에러 코드는 HTTP 상태와 메시지를 포함하여 명확한 의미를 제공함
 */
public enum ErrorCode {
    
    // 서버 내부 오류 (5xx)
    UNEXPECTED_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에 문제가 발생했습니다. 지원팀에 문의해주세요"),
    
    // 인증/권한 오류 (4xx)
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "유효한 인증 자격 증명이 필요합니다"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다"),
    
    // 클라이언트 요청 오류 (4xx)
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 정보를 찾을 수 없습니다"),
    INVALID_REQUEST_FORMAT(HttpStatus.BAD_REQUEST, "요청 형식이 올바르지 않습니다"),
    MISSING_REQUIRED_PARAMETER(HttpStatus.BAD_REQUEST, "필수 정보가 누락되었습니다"),
    
    // 입력값 검증 오류 (4xx)
    VALIDATION_FAILED(HttpStatus.BAD_REQUEST, "입력하신 정보를 다시 확인해주세요"),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "올바르지 않은 값이 입력되었습니다"),
    REQUIRED_VALUE_MISSING(HttpStatus.BAD_REQUEST, "필수 입력값이 누락되었습니다", KEYWORD_NOT_BLANK),

    // 상품 도메인 검증 오류 (4xx)
    PRODUCT_NAME_TOO_LONG(HttpStatus.BAD_REQUEST, "상품명은 15자 이하로 입력해주세요", NAME_KEYWORD_SIZE),
    PRODUCT_NAME_INVALID_CHARS(HttpStatus.BAD_REQUEST, "상품명에 사용할 수 없는 문자가 포함되어 있습니다", NAME_KEYWORD_PATTERN),
    PRODUCT_NAME_KAKAO_RESTRICTED(HttpStatus.BAD_REQUEST, "카카오가 포함된 상품명은 사전 승인이 필요합니다", NAME_KEYWORD_KAKAO),
    PRODUCT_PRICE_REQUIRED(HttpStatus.BAD_REQUEST, "상품 가격은 필수 입력값입니다"),
    PRODUCT_PRICE_INVALID(HttpStatus.BAD_REQUEST, "상품 가격을 올바르게 입력해주세요", PRICE_KEYWORD_MIN),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품 정보를 찾을 수 없습니다");

    private final HttpStatus httpStatus;
    private final String message;
    private final String[] keywords;

    ErrorCode(HttpStatus httpStatus, String message, String... keywords) {
        this.httpStatus = httpStatus;
        this.message = message;
        this.keywords = keywords;
    }

    public static ErrorCode fromMessage(String message) {
        if (message == null || message.isBlank()) {
            return VALIDATION_FAILED;
        }

        return Arrays.stream(values())
            .filter(errorCode -> errorCode.hasKeywords())
            .filter(errorCode -> errorCode.matches(message))
            .findFirst()
            .orElse(VALIDATION_FAILED);
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
    
    public int getStatusCode() {
        return httpStatus.value();
    }
    
    /**
     * 서버 오류인지 확인 (5xx)
     */
    public boolean isServerError() {
        return httpStatus.is5xxServerError();
    }

    /**
     * 클라이언트 오류인지 확인 (4xx)
     */
    public boolean isClientError() {
        return httpStatus.is4xxClientError();
    }

    private boolean hasKeywords() {
        return keywords != null && keywords.length > 0;
    }

    private boolean matches(String message) {
        return Arrays.stream(keywords)
            .anyMatch(message::contains);
    }
}