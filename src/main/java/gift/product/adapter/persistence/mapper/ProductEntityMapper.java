package gift.product.adapter.persistence.mapper;

import gift.product.adapter.persistence.entity.OptionEntity;
import gift.product.adapter.persistence.entity.ProductEntity;
import gift.product.domain.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class ProductEntityMapper {

    private static final Logger log = LoggerFactory.getLogger(ProductEntityMapper.class);

    public static Product toDomain(ProductEntity entity) {
        if (entity == null) {
            return null;
        }
        return Product.of(
                entity.getId(),
                entity.getName(),
                entity.getPrice(),
                entity.getImageUrl(),
                entity.getOptions().stream()
                    .map(OptionEntityMapper::toDomain)
                    .collect(Collectors.toList())
        );
    }

    public static ProductEntity toEntity(Product domain) {
        log.info("3. Mapper: toEntity 호출. Product domain: {}", domain);
        if (domain == null) {
            return null;
        }

        List<OptionEntity> optionEntities = domain.getOptions().stream()
                .map(optionDomain -> OptionEntityMapper.toEntity(optionDomain, null))
                .collect(Collectors.toList());
        log.info("3.1. Mapper: OptionEntities 변환 완료. count: {}", optionEntities);

        if (domain.getId() == null) {
            return ProductEntity.create(
                    domain.getName(),
                    domain.getPrice(),
                    domain.getImageUrl(),
                    optionEntities
            );
        }

        return ProductEntity.of(
                domain.getId(),
                domain.getName(),
                domain.getPrice(),
                domain.getImageUrl(),
                optionEntities
        );
    }
} 