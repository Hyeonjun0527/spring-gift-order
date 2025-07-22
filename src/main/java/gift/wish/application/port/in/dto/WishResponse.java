package gift.wish.application.port.in.dto;

public record WishResponse(
        Long wishId,
        Long productId,
        String productName,
        int productPrice,
        String productImageUrl,
        int quantity
) {
} 