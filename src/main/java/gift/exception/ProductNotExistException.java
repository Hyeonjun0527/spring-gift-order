package gift.exception;

public class ProductNotExistException extends RuntimeException {
    public ProductNotExistException(Long productId) {
        super("상품 ID " + productId + "에 해당하는 상품이 존재하지 않습니다.");
    }
}
