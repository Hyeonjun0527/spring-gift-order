package gift.exception;

public class WishNotExistException extends RuntimeException {
    public WishNotExistException(Long productId) {
        super("위시 ID " + productId + "에 해당하는 위시가 존재하지 않습니다.");
    }
}
