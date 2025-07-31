package gift.order.application.port.in.dto;

import gift.order.domain.model.Order;

import java.time.LocalDateTime;

public record OrderResponse(
        Long id,
        Long optionId,
        int quantity,
        LocalDateTime orderDateTime,
        String message
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.id(),
                order.optionId(),
                order.quantity(),
                order.orderDateTime(),
                order.message()
        );
    }
} 