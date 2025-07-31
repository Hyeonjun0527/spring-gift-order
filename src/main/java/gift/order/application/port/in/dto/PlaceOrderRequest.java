package gift.order.application.port.in.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PlaceOrderRequest(
        @NotNull(message = "옵션 ID는 필수입니다.")
        Long optionId,

        @NotNull(message = "수량은 필수입니다.")
        @Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
        int quantity,

        String message
) {
} 