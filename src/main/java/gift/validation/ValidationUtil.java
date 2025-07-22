package gift.validation;

import gift.dto.ProductRequest;
import gift.dto.WishRequest;
import gift.entity.Member;
import gift.exception.InvalidFieldException;
import gift.exception.ValidationException;

public class ValidationUtil {

    public static void validateWishRequestAndMember(WishRequest request, Member member) {
        if (request == null) {
            throw new ValidationException("Request cannot be null");
        }
        if (member == null) {
            throw new ValidationException("Member cannot be null");
        }
        if (request.productId() == null) {
            throw new InvalidFieldException("Invalid productId");
        }
        if (request.quantity() == null || request.quantity() <= 0) {
            throw new InvalidFieldException("Invalid quantity");
        }
    }

    public static void validatePIDAndMember(Long productId, Member member) {
        if (productId == null) {
            throw new ValidationException("ProductId cannot be null");
        }
        if (member == null) {
            throw new ValidationException("Member cannot be null");
        }
    }

    public static void validateProductRequest(ProductRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        if (request.name() == null) {
            throw new IllegalArgumentException("상품명은 필수입니다.");
        }
        if (request.name().length() > 15) {
            throw new IllegalArgumentException("상품 이름은 최대 15자까지 입력 가능합니다.");
        }
        if (!request.name().matches("^[a-zA-Z0-9\\s\\(\\)\\[\\]\\+\\-&/_\\uAC00-\\uD7AF]+$")) {
            throw new IllegalArgumentException("허용되지 않은 특수 문자가 포함되었습니다.");
        }
        if (request.name().contains("카카오")) {
            throw new IllegalArgumentException("상품명에 '카카오'가 포함되었습니다. 담당자와 협의가 필요합니다.");
        }
        if (request.price() == null) {
            throw new IllegalArgumentException("가격은 필수입니다.");
        }
    }
}
