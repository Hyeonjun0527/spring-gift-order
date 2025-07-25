package gift.member.domain.model;

import java.time.LocalDateTime;

public record Member(Long id, String email, String password, Role role, LocalDateTime createdAt, Long kakaoId) {

    public Member {
        if (role == null) {
            role = Role.USER;
        }
    }

    public static Member of(Long id, String email, String password, Role role, LocalDateTime createdAt, Long kakaoId) {
        return new Member(id, email, password, role, createdAt, kakaoId);
    }

    public static Member create(String email, String password) {
        return new Member(null, email, password, Role.USER, LocalDateTime.now(), null);
    }

    public Member withKakaoId(Long newKakaoId) {
        return new Member(this.id, this.email, this.password, this.role, this.createdAt, newKakaoId);
    }
} 