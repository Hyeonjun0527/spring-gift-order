package gift.order.adapter.persistence.adapter;

import gift.member.adapter.persistence.entity.MemberEntity;
import gift.order.adapter.persistence.entity.OrderEntity;
import gift.order.adapter.persistence.mapper.OrderEntityMapper;
import gift.order.adapter.persistence.repository.OrderJpaRepository;
import gift.order.application.port.out.OrderRepository;
import gift.order.domain.model.Order;
import gift.product.adapter.persistence.entity.OptionEntity;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Component;

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
        // 1. ID를 사용하여 영속성 컨텍스트로부터 엔티티의 '프록시(참조)'를 가져옵니다.
        // 이 방식은 실제 SELECT 쿼리를 날리지 않고도 연관관계를 설정할 수 있게 해줍니다.
        MemberEntity memberEntityProxy = entityManager.getReference(MemberEntity.class, order.memberId());
        OptionEntity optionEntityProxy = entityManager.getReference(OptionEntity.class, order.optionId());

        // 2. 프록시를 사용하여 완전한 OrderEntity를 생성하고 저장합니다.
        OrderEntity orderEntity = OrderEntityMapper.toEntity(order, memberEntityProxy, optionEntityProxy);
        OrderEntity savedEntity = orderJpaRepository.save(orderEntity);
        return OrderEntityMapper.toDomain(savedEntity);
    }
} 