package gift.order.adapter.web;

import gift.common.config.AppProperties;
import gift.common.config.KakaoProperties;
import gift.common.exception.KakaoApiClientException;
import gift.order.application.port.out.KakaoMessagePort;
import gift.order.domain.model.Order;
import gift.product.domain.model.Option;
import gift.product.domain.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
public class KakaoMessageApiClient implements KakaoMessagePort {

    private static final Logger log = LoggerFactory.getLogger(KakaoMessageApiClient.class);
    private final RestClient restClient;
    private final KakaoProperties kakaoProperties;
    private final AppProperties appProperties;

    public KakaoMessageApiClient(RestClient.Builder restClientBuilder, KakaoProperties kakaoProperties, AppProperties appProperties) {
        this.restClient = restClientBuilder.build();
        this.kakaoProperties = kakaoProperties;
        this.appProperties = appProperties;
    }

    @Override
    public void sendOrderConfirmationMessage(String accessToken, Order order, Product product, Option option) {
        String messageJson = createMessageJson(order, product, option);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("template_object", messageJson);

        restClient.post()
                .uri(kakaoProperties.getMessageMeUri())
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    log.error("[Kakao API] Message - 4xx Error: {}, Body: {}", response.getStatusText(), response.getBody());
                    throw new KakaoApiClientException("카카오 메시지 발송 중 클라이언트 오류가 발생했습니다.");
                })
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                    log.error("[Kakao API] Message - 5xx Error: {}", response.getStatusText());
                    throw new KakaoApiClientException("카카오 서버 오류로 메시지 발송에 실패했습니다.");
                })
                .toBodilessEntity();

        log.info("카카오톡 주문 메시지 발송 성공. Order ID: {}", order.id());
    }

    private String createMessageJson(Order order, Product product, Option option) {
        String baseUrl = appProperties.getBaseUrl();
        // Kakao의 기본 템플릿 형식에 맞춰 JSON 문자열 생성
        // 더 복잡한 템플릿을 원할 경우 이 부분을 수정
        return String.format("""
                {
                    "object_type": "feed",
                    "content": {
                        "title": "주문이 완료되었습니다.",
                        "description": "%s",
                        "image_url": "%s",
                        "link": {
                            "web_url": "%s",
                            "mobile_web_url": "%s"
                        }
                    },
                    "item_content": {
                        "profile_text": "Spring Gift",
                        "items": [
                            { "item": "상품명", "item_op": "%s" },
                            { "item": "옵션", "item_op": "%s" },
                            { "item": "수량", "item_op": "%d개" },
                            { "item": "결제 금액", "item_op": "%,d원" }
                        ]
                    },
                    "buttons": [
                        {
                            "title": "주문 내역 확인",
                            "link": {
                                "web_url": "%s",
                                "mobile_web_url": "%s"
                            }
                        }
                    ]
                }
                """,
                order.message(),
                product.getImageUrl(),
                baseUrl,
                baseUrl,
                product.getName(),
                option.getName(),
                order.quantity(),
                (long) product.getPrice() * order.quantity(),
                baseUrl,
                baseUrl
        );
    }
} 