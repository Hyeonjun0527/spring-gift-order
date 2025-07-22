package gift.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record MemberLoginRequestDto (
        @Email
        @NotBlank
        String email,

        @NotBlank
        String password) {
}
