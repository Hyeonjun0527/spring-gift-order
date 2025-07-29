package gift.product.application.port.in.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateOptionRequest(
        @NotBlank
        @Size(max = 100, message = "옵션 이름은 100자를 초과할 수 없습니다.")
        String name,

        @NotNull
        @Min(value = 1, message = "옵션 수량은 1개 이상이어야 합니다.")
        Integer quantity
) {
} 