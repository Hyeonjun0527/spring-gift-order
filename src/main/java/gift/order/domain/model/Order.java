package gift.order.domain.model;

import java.time.LocalDateTime;

public record Order(
        Long id,
        Long memberId,
        Long optionId,
        String productName,
        String optionName,
        int price,
        int quantity,
        String message,
        LocalDateTime orderDateTime
) {
    public static Order create(Long memberId, Long optionId, String productName, String optionName, int price, int quantity, String message) {
        return new Order(null, memberId, optionId, productName, optionName, price, quantity, message, LocalDateTime.now());
    }

    public static Order of(Long id, Long memberId, Long optionId, String productName, String optionName, int price, int quantity, String message, LocalDateTime orderDateTime) {
        return new Order(id, memberId, optionId, productName, optionName, price, quantity, message, orderDateTime);
    }
} 