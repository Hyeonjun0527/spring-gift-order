package gift.member.adapter.persistence.mapper;

import gift.member.adapter.persistence.entity.KakaoInfoEmbeddable;
import gift.member.adapter.persistence.entity.MemberEntity;
import gift.member.domain.model.KakaoInfo;
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
                toKakaoInfoDomain(entity.getKakaoInfo())
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
                toKakaoInfoEntity(domain.kakaoInfo())
        );
    }

    private static KakaoInfo toKakaoInfoDomain(KakaoInfoEmbeddable embeddable) {
        if (embeddable == null) {
            return null;
        }
        return new KakaoInfo(
                embeddable.getKakaoId(),
                embeddable.getAccessToken(),
                embeddable.getRefreshToken()
        );
    }

    private static KakaoInfoEmbeddable toKakaoInfoEntity(KakaoInfo domain) {
        if (domain == null) {
            return null;
        }
        return new KakaoInfoEmbeddable(
                domain.kakaoId(),
                domain.accessToken(),
                domain.refreshToken()
        );
    }
} 