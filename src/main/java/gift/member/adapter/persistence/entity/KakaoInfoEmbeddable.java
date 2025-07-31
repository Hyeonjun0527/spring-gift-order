package gift.member.adapter.persistence.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public class KakaoInfoEmbeddable {

    private Long kakaoId;
    private String accessToken;
    private String refreshToken;

    protected KakaoInfoEmbeddable() {
    }

    public KakaoInfoEmbeddable(Long kakaoId, String accessToken, String refreshToken) {
        this.kakaoId = kakaoId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }

    public Long getKakaoId() {
        return kakaoId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }
} 