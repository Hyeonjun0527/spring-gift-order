package gift.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record MemberRequestDto(
        @NotBlank(message = "이름은 필수 입력값입니다.")
        String name,

        @Email
        @NotBlank(message = "이메일은 필수 입력값입니다.")
        String email,

        @Size(min = 8, max = 20,
                message = "비밀번호는 8자 이상 20자 이하로 설정해주세요.")
        @NotBlank(message = "비밀번호는 필수 입력값입니다.")
        String password
) {
}
