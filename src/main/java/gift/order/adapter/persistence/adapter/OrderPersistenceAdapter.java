package gift.order.adapter.persistence.adapter;

import gift.member.adapter.persistence.entity.MemberEntity;
import gift.order.adapter.persistence.entity.OrderEntity;
import gift.order.adapter.persistence.mapper.OrderEntityMapper;
import gift.order.adapter.persistence.repository.OrderJpaRepository;
import gift.order.domain.model.Order;
import gift.order.domain.port.out.OrderRepository;
import gift.product.adapter.persistence.entity.OptionEntity;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class OrderPersistenceAdapter implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;
    private final EntityManager entityManager;

    public OrderPersistenceAdapter(OrderJpaRepository orderJpaRepository, EntityManager entityManager) {
        this.orderJpaRepository = orderJpaRepository;
        this.entityManager = entityManager;
    }

    @Override
    public Order save(Order order) {
        MemberEntity memberEntityProxy = entityManager.getReference(MemberEntity.class, order.memberId());
        OptionEntity optionEntityProxy = entityManager.getReference(OptionEntity.class, order.optionId());

        OrderEntity orderEntity = OrderEntityMapper.toEntity(order, memberEntityProxy, optionEntityProxy);
        OrderEntity savedEntity = orderJpaRepository.save(orderEntity);
        return OrderEntityMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Order> findById(Long orderId) {
        return orderJpaRepository.findById(orderId)
                .map(OrderEntityMapper::toDomain);
    }
} 