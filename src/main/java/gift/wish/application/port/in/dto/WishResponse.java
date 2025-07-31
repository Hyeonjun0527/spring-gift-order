package gift.wish.application.port.in.dto;

public record WishResponse(
        Long wishId,
        Long productId,
        Long optionId,
        String productName,
        String optionName,
        int productPrice,
        String productImageUrl,
        int quantity
) {
} 