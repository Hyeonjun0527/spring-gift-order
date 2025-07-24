package gift.wish.application.port.in.dto;

import jakarta.validation.constraints.Min;

public record WishUpdateQuantityRequest(
        @Min(1)
        int quantity
) {
} 