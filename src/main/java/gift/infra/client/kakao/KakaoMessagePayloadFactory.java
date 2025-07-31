package gift.infra.client.kakao;

import gift.common.config.AppProperties;
import gift.order.domain.model.Order;
import gift.product.domain.model.Option;
import gift.product.domain.model.Product;
import org.springframework.stereotype.Component;

@Component
public class KakaoMessagePayloadFactory {

    private final AppProperties appProperties;

    public KakaoMessagePayloadFactory(AppProperties appProperties) {
        this.appProperties = appProperties;
    }

    public String createOrderConfirmationMessageJson(Order order, Product product, Option option) {
        String baseUrl = appProperties.getBaseUrl();
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