package gift.order.application.port.out;

import gift.order.domain.model.Order;
import gift.product.domain.model.Option;
import gift.product.domain.model.Product;

public interface KakaoMessagePort {
    void sendOrderConfirmationMessage(String accessToken, Order order, Product product, Option option);
} 