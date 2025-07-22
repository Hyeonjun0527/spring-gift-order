package gift.member.application.port.in.dto;

import gift.member.domain.model.Role;
import jakarta.validation.constraints.Email;

public record UpdateMemberRequest(
        @Email(message = "올바른 이메일 형식이 아닙니다")
        String email,
        String password,
        Role role
) {
} 