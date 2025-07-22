package gift.member.application.port.in.dto;

import gift.member.domain.model.Role;

import java.time.LocalDateTime;

public record MemberResponse(
        Long id,
        String email,
        Role role,
        LocalDateTime createdAt
) {
} 