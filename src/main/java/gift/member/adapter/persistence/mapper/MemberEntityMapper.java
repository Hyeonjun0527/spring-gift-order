package gift.member.adapter.persistence.mapper;

import gift.member.adapter.persistence.entity.MemberEntity;
import gift.member.domain.model.Member;

public class MemberEntityMapper {

    public static Member toDomain(MemberEntity entity) {
        if (entity == null) {
            return null;
        }
        return Member.of(
                entity.getId(),
                entity.getEmail(),
                entity.getPassword(),
                entity.getRole(),
                entity.getCreatedAt(),
                entity.getKakaoId()
        );
    }

    public static MemberEntity toEntity(Member domain) {
        if (domain == null) {
            return null;
        }
        return new MemberEntity(
                domain.id(),
                domain.email(),
                domain.password(),
                domain.role(),
                domain.createdAt(),
                domain.kakaoId()
        );
    }
} 