package gift.dto;

import gift.validation.ForbiddenWord;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public record ProductRequestDto(
        @NotBlank(message = "상품명은 필수 입력값입니다.")
        @Size(max = 15, message = "상품명은 15자 이하로 입력해주세요.")
        @Pattern(
                regexp = "^[a-zA-Z0-9가-힣()\\[\\]+\\-\\&/_\\s]*$",
                message = "특수문자는 ()[]+-&/_ 만 사용할 수 있어요."
        )
        @ForbiddenWord(word = "카카오", message = "상품명에 <카카오>가 포함된 상품은 담당 MD에게 문의해주세요.")
        String name,

        @NotNull(message = "가격은 필수 입력값입니다.")
        @Min(value = 100, message = "가격은 100원 이상으로 등록해주세요.")
        Integer price,

        String imageUrl,

        @NotEmpty(message = "옵션은 반드시 하나 이상 등록해야 합니다.")
        List<@Valid OptionRequestDto> options
) {}