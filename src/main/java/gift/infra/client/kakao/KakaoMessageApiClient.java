package gift.infra.client.kakao;

import gift.common.config.KakaoProperties;
import gift.common.exception.KakaoApiClientException;
import gift.order.application.port.out.KakaoMessagePort;
import gift.order.domain.model.Order;
import gift.product.domain.model.Option;
import gift.product.domain.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.io.IOException;

@Component
public class KakaoMessageApiClient implements KakaoMessagePort {

    private static final Logger log = LoggerFactory.getLogger(KakaoMessageApiClient.class);
    private final RestClient restClient;
    private final KakaoProperties kakaoProperties;
    private final KakaoMessagePayloadFactory kakaoMessagePayloadFactory;

    public KakaoMessageApiClient(@Qualifier("kakaoRestClient") RestClient restClient,
        KakaoProperties kakaoProperties,
        KakaoMessagePayloadFactory payloadFactory) {
        this.restClient = restClient;
        this.kakaoProperties = kakaoProperties;
        this.kakaoMessagePayloadFactory = payloadFactory;
    }

    @Override
    public void sendOrderConfirmationMessage(String accessToken, Order order, Product product, Option option) {
        String messageJson = kakaoMessagePayloadFactory.createOrderConfirmationMessageJson(order, product, option);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("template_object", messageJson);

        ResponseEntity<String> response = restClient.post()
            .uri(kakaoProperties.getMessageMeUri())
            .header("Authorization", "Bearer " + accessToken)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(body)
            .retrieve()
            .onStatus(statusCode -> statusCode.isError(), this::handleClientError)
            .toEntity(String.class);

        log.info("카카오톡 주문 메시지 API 응답. status: {}, body: {}", response.getStatusCode(),
            response.getBody());

        if (response.getBody() != null && response.getBody().contains("\"result_code\":0")) {
            log.info("카카오톡 주문 메시지 발송 성공. Order ID: {}", order.id());
        } else {
            log.error("카카오톡 주문 메시지 발송 실패. 응답 본문: {}", response.getBody());
            throw new KakaoApiClientException("카카오톡 메시지 발송에 실패했습니다. 응답: " + response.getBody());
        }
    }

    private void handleClientError(HttpRequest request, ClientHttpResponse response) {
        String responseBody = readResponseBody(response);
        String statusCode = getStatusCodeAsString(response);

        log.error("카카오 메시지 전송 실패. status: {}, body: {}", statusCode, responseBody);
        throw new KakaoApiClientException("카카오 메시지 전송에 실패했습니다. 응답: " + responseBody);
    }

    private String getStatusCodeAsString(ClientHttpResponse response) {
        try {
            return response.getStatusCode().toString();
        } catch (IOException e) {
            log.error("카카오 API 응답 상태 코드를 읽는 데 실패했습니다.", e);
            return "UNKNOWN";
        }
    }

    private String readResponseBody(ClientHttpResponse response) {
        try {
            return new String(response.getBody().readAllBytes());
        } catch (IOException e) {
            throw new KakaoApiClientException("카카오 API 응답 본문을 읽는 중 오류가 발생했습니다.", e);
        }
    }

} 