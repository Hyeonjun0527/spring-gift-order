package gift.member.domain.model;

import java.time.LocalDateTime;

public record Member(
        Long id,
        String email,
        String password,
        Role role,
        LocalDateTime createdAt,
        KakaoInfo kakaoInfo
) {

    public Member {
        if (role == null) {
            role = Role.USER;
        }
    }

    public static Member create(String email, String password) {
        return new Member(null, email, password, Role.USER, LocalDateTime.now(), null);
    }

    public static Member of(Long id, String email, String password, Role role, LocalDateTime createdAt, KakaoInfo kakaoInfo) {
        return new Member(id, email, password, role, createdAt, kakaoInfo);
    }

    public Member withKakaoInfo(KakaoInfo newKakaoInfo) {
        return new Member(this.id, this.email, this.password, this.role, this.createdAt, newKakaoInfo);
    }

    public Member withKakaoTokens(String accessToken, String refreshToken) {
        KakaoInfo newKakaoInfo = this.kakaoInfo != null ? this.kakaoInfo.withTokens(accessToken, refreshToken) : null;
        return new Member(this.id, this.email, this.password, this.role, this.createdAt, newKakaoInfo);
    }
} 