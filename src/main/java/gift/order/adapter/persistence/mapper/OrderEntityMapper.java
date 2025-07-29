package gift.order.adapter.persistence.mapper;

import gift.member.adapter.persistence.entity.MemberEntity;
import gift.order.adapter.persistence.entity.OrderEntity;
import gift.order.domain.model.Order;
import gift.product.adapter.persistence.entity.OptionEntity;

public class OrderEntityMapper {

    public static Order toDomain(OrderEntity entity) {
        if (entity == null) {
            return null;
        }
        return Order.of(
                entity.getId(),
                entity.getMember().getId(),
                entity.getOption().getId(),
                entity.getProductName(),
                entity.getOptionName(),
                entity.getPrice(),
                entity.getQuantity(),
                entity.getMessage(),
                entity.getOrderDateTime()
        );
    }

    public static OrderEntity toEntity(Order domain, MemberEntity memberEntity, OptionEntity optionEntity) {
        if (domain == null) {
            return null;
        }
        return new OrderEntity(
                domain.id(),
                memberEntity,
                optionEntity,
                domain.productName(),
                domain.optionName(),
                domain.price(),
                domain.quantity(),
                domain.message(),
                domain.orderDateTime()
        );
    }
} 