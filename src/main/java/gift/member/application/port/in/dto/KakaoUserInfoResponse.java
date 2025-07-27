package gift.member.application.port.in.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record KakaoUserInfoResponse(
        @JsonProperty("id") Long id,
        @JsonProperty("connected_at") String connectedAt,
        @JsonProperty("properties") Map<String, String> properties,
        @JsonProperty("kakao_account") KakaoAccount kakaoAccount
) {
    public String getEmail() {
        return this.kakaoAccount.email();
    }

    public String getNickname() {
        return this.kakaoAccount.profile().nickname();
    }

    public record KakaoAccount(
            @JsonProperty("profile") Profile profile,
            @JsonProperty("email") String email
    ) {
    }

    public record Profile(
            @JsonProperty("nickname") String nickname
    ) {
    }
} 