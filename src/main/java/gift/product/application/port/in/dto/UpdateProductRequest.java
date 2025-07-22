package gift.product.application.port.in.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

import static gift.common.validation.ValidationMessages.*;

public record UpdateProductRequest(
        @NotBlank(message = NOT_BLANK_MESSAGE)
        @Size(max = 15, message = NAME_SIZE_MESSAGE)
        @Pattern(regexp = "^[가-힣a-zA-Z0-9_()&+\\-\\[\\] ]*$", message = NAME_PATTERN_MESSAGE)
        @Pattern(regexp = "^(?!.*카카오).*$", message = NAME_KAKAO_MESSAGE)
        String name,

        @NotNull(message = PRICE_NOT_NULL_MESSAGE)
        @Min(value = 1, message = PRICE_MIN_MESSAGE)
        Integer price,

        String imageUrl,

        @NotEmpty(message = "상품에는 하나 이상의 옵션이 있어야 합니다.")
        List<@Valid OptionRequest> options
) {
} 