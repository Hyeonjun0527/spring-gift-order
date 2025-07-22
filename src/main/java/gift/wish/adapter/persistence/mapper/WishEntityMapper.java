package gift.wish.adapter.persistence.mapper;

import gift.member.adapter.persistence.mapper.MemberEntityMapper;
import gift.product.adapter.persistence.mapper.ProductEntityMapper;
import gift.wish.adapter.persistence.entity.WishEntity;
import gift.wish.domain.model.Wish;

public class WishEntityMapper {
    public static Wish toDomain(WishEntity entity) {
        if (entity == null) return null;
        return Wish.of(
                entity.getId(),
                MemberEntityMapper.toDomain(entity.getMember()),
                ProductEntityMapper.toDomain(entity.getProduct()),
                entity.getQuantity()
        );
    }

    public static WishEntity toEntity(Wish domain) {
        if (domain == null) return null;
        return WishEntity.of(
                domain.getId(),
                MemberEntityMapper.toEntity(domain.getMember()),
                ProductEntityMapper.toEntity(domain.getProduct()),
                domain.getQuantity()
        );
    }
}
