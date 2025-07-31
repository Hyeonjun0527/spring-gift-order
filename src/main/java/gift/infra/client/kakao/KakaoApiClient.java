package gift.infra.client.kakao;

import gift.common.config.KakaoProperties;
import gift.common.exception.KakaoApiClientException;
import gift.member.application.port.in.dto.KakaoTokenResponse;
import gift.member.application.port.in.dto.KakaoUserInfoResponse;
import gift.member.application.port.out.KakaoAuthPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.io.IOException;

@Component
public class KakaoApiClient implements KakaoAuthPort {

    private static final Logger log = LoggerFactory.getLogger(KakaoApiClient.class);
    private final RestClient restClient;
    private final KakaoProperties kakaoProperties;

    public KakaoApiClient(@Qualifier("kakaoRestClient") RestClient restClient,
        KakaoProperties kakaoProperties) {
        this.restClient = restClient;
        this.kakaoProperties = kakaoProperties;
    }

    @Override
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
            .onStatus(statusCode -> statusCode.isError(), (request, response) -> {
                String responseBody;
                try {
                    responseBody = new String(response.getBody().readAllBytes());
                } catch (IOException e) {
                    throw new KakaoApiClientException("카카오 API 응답을 읽는 중 오류가 발생했습니다.", e);
                }
                log.error("카카오 토큰 요청 실패. status: {}, body: {}", response.getStatusCode(),
                    responseBody);
                throw new KakaoApiClientException("카카오 토큰 요청에 실패했습니다. 응답: " + responseBody);
            })
            .body(KakaoTokenResponse.class);
    }

    @Override
    public KakaoUserInfoResponse fetchUserInfo(String accessToken) {
        return restClient.get()
            .uri(kakaoProperties.getUserInfoUri())
            .header("Authorization", "Bearer " + accessToken)
            .retrieve()
            .onStatus(statusCode -> statusCode.isError(), (request, response) -> {
                String responseBody;
                try {
                    responseBody = new String(response.getBody().readAllBytes());
                } catch (IOException e) {
                    throw new KakaoApiClientException("카카오 API 응답을 읽는 중 오류가 발생했습니다.", e);
                }
                log.error("카카오 사용자 정보 요청 실패. status: {}, body: {}", response.getStatusCode(),
                    responseBody);
                throw new KakaoApiClientException("카카오 사용자 정보 요청에 실패했습니다. 응답: " + responseBody);
            })
            .body(KakaoUserInfoResponse.class);
    }
} 