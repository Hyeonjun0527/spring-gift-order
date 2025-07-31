package gift.common.exception;

public class KakaoApiClientException extends RuntimeException {

    public KakaoApiClientException(String message) {
        super(message);
    }

    public KakaoApiClientException(String message, Throwable cause) {
        super(message, cause);
    }
} 