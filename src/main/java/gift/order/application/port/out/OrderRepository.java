package gift.order.application.port.out;

import gift.order.domain.model.Order;

public interface OrderRepository {
    Order save(Order order);
} 