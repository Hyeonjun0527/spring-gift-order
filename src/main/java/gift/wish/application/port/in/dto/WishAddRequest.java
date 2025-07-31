package gift.wish.application.port.in.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record WishAddRequest(
        @NotNull
        @Min(1)
        Long optionId,

        @NotNull
        @Min(1)
        int quantity
) {
} 