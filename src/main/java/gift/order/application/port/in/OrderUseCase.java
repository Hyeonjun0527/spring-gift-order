package gift.order.application.port.in;

import gift.order.application.port.in.dto.OrderResponse;
import gift.order.application.port.in.dto.PlaceOrderRequest;

public interface OrderUseCase {
    OrderResponse placeOrder(Long memberId, PlaceOrderRequest request);
} 