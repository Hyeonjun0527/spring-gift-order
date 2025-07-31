package gift.member.domain.model;

public record KakaoInfo(
    Long kakaoId,
    String accessToken,
    String refreshToken
) {

    public KakaoInfo(Long kakaoId) {
        this(kakaoId, null, null);
    }

    public KakaoInfo withTokens(String newAccessToken, String newRefreshToken) {
        return new KakaoInfo(this.kakaoId, newAccessToken, newRefreshToken);
    }
} 