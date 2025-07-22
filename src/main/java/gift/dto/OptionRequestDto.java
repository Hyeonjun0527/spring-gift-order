package gift.dto;

import jakarta.validation.constraints.*;

public record OptionRequestDto(
        @NotBlank(message = "옵션명은 필수 입력값입니다.")
        @Size(max = 50, message = "옵션명은 50자 이하로 입력해주세요.")
        @Pattern(
                regexp = "^[a-zA-Z0-9가-힣()\\[\\]+\\-\\&/_\\s]*$",
                message = "특수문자는 ()[]+-&/_ 만 사용할 수 있어요."
        )
        String name,

        @NotNull(message = "가격은 필수 입력값입니다.")
        @Min(value = 1, message = "수량은 1개 이상으로 등록해주세요.")
        @Max(value = 99999999, message = "수량은 1억개 미만으로 등록해주세요.")
        Integer quantity
) {
}
