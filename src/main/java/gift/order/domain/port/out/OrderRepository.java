package gift.order.domain.port.out;

import gift.order.domain.model.Order;

import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(Long orderId);
} 