package gift.product.application.port.in.dto;

import jakarta.validation.constraints.*;

public record OptionRequest(

        @NotBlank
        @Size(max = 50, message = "옵션명은 50자 이하여야합니다.")
        @Pattern(regexp = "^[a-zA-Z0-9가-힣\\s\\(\\)\\[\\]\\+\\-\\&\\/\\_]*$", message = "허용되지 않은 문자입니다.")
        String name,

        @Min(value = 1, message = "1개 이상이어야 합니다.")
        @Max(value = 99999999, message = "1억개 미만이어야 합니다.")
        int quantity

) {
}
