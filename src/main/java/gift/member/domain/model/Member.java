package gift.member.domain.model;

import java.time.LocalDateTime;

public record Member(
        Long id,
        String email,
        String password,
        Role role,
        LocalDateTime createdAt,
        Long kakaoId,
        String kakaoAccessToken,
        String kakaoRefreshToken
) {

    public Member {
        if (role == null) {
            role = Role.USER;
        }
    }

    public static Member create(String email, String password) {
        return new Member(null, email, password, Role.USER, LocalDateTime.now(), null, null, null);
    }

    public static Member of(Long id, String email, String password, Role role, LocalDateTime createdAt, Long kakaoId) {
        return new Member(id, email, password, role, createdAt, kakaoId, null, null);
    }

    public Member withKakaoId(Long newKakaoId) {
        return new Member(this.id, this.email, this.password, this.role, this.createdAt, newKakaoId, this.kakaoAccessToken, this.kakaoRefreshToken);
    }

    public Member withKakaoTokens(String accessToken, String refreshToken) {
        return new Member(this.id, this.email, this.password, this.role, this.createdAt, this.kakaoId, accessToken, refreshToken);
    }
} 