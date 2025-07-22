package gift.wish.application.port.in.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record WishAddRequest(
        @NotNull
        @Min(1)
        Long productId,

        @NotNull
        @Min(1)
        int quantity
) {
} 