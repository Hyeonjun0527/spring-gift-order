package gift.product.application.port.in.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateOptionRequest(
        Long id, // 기존 옵션의 ID (없으면 신규)

        @NotBlank
        @Size(max = 150)
        String name,

        @NotNull
        @Min(1)
        Integer quantity
) {
} 