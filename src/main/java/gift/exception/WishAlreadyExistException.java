package gift.exception;

public class WishAlreadyExistException extends RuntimeException {
    public WishAlreadyExistException(Long productId) {
        super("상품 ID " + productId + "은(는) 이미 위시리스트에 있습니다.");
    }
}