package gift.product.application.port.in.dto;

import jakarta.validation.constraints.*;

import java.util.List;

import static gift.common.validation.ValidationMessages.*;

public record CreateProductRequest(
        @NotBlank(message = NOT_BLANK_MESSAGE)
        @Size(max = 15, message = NAME_SIZE_MESSAGE)
        @Pattern(regexp = "^[가-힣a-zA-Z0-9\\s()\\[\\]+&/_-]*$", message = NAME_PATTERN_MESSAGE)
        @Pattern(regexp = "^(?!.*카카오).*$", message = NAME_KAKAO_MESSAGE)
        String name,

        @NotNull(message = PRICE_NOT_NULL_MESSAGE)
        @Min(value = 1, message = PRICE_MIN_MESSAGE)
        int price,

        String imageUrl,

        List<OptionRequest> optionRequests
) {

        public static CreateProductRequest of(String name, int price, String imageUrl, List<OptionRequest> optionRequests) {
                return new CreateProductRequest(name, price, imageUrl, optionRequests);
        }
}
