package gift.member.adapter.web;

import gift.common.config.KakaoProperties;
import gift.member.application.port.in.dto.KakaoTokenResponse;
import gift.member.application.port.in.dto.KakaoUserInfoResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
public class KakaoApiClient {

    private final RestClient restClient;
    private final KakaoProperties kakaoProperties;

    public KakaoApiClient(RestClient.Builder restClientBuilder, KakaoProperties kakaoProperties) {
        this.restClient = restClientBuilder.build();
        this.kakaoProperties = kakaoProperties;
    }

    public KakaoTokenResponse fetchToken(String authCode) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoProperties.getClientId());
        body.add("redirect_uri", kakaoProperties.getRedirectUri());
        body.add("code", authCode);
        body.add("client_secret", kakaoProperties.getClientSecret());

        return restClient.post()
                .uri(kakaoProperties.getTokenUri())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(KakaoTokenResponse.class);
    }

    public KakaoUserInfoResponse fetchUserInfo(String accessToken) {
        return restClient.get()
                .uri(kakaoProperties.getUserInfoUri())
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(KakaoUserInfoResponse.class);
    }
} 