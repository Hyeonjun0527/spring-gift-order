package gift.product.adapter.persistence.mapper;

import gift.product.adapter.persistence.entity.OptionEntity;
import gift.product.adapter.persistence.entity.ProductEntity;
import gift.product.domain.model.Option;

public class OptionEntityMapper {

    public static Option toDomain(OptionEntity entity) {
        if (entity == null) return null;

        return Option.of(
                entity.getId(),
                entity.getProduct().getId(),
                entity.getName(),
                entity.getQuantity()
        );
    }

    public static OptionEntity toEntity(Option domain, ProductEntity parent) {
        if (domain == null) return null;
        return OptionEntity.of(
                domain.getId(),
                domain.getName(),
                domain.getQuantity(),
                parent
        );
    }
}
