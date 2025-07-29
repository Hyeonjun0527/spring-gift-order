package gift.product.adapter.persistence.adapter;

import gift.product.adapter.persistence.entity.ProductEntity;
import gift.product.adapter.persistence.mapper.OptionEntityMapper;
import gift.product.adapter.persistence.repository.OptionJpaRepository;
import gift.product.domain.model.Option;
import gift.product.domain.port.out.OptionRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class OptionPersistenceAdapter implements OptionRepository {

    private final OptionJpaRepository optionJpaRepository;
    private final EntityManager entityManager;

    public OptionPersistenceAdapter(OptionJpaRepository optionJpaRepository, EntityManager entityManager) {
        this.optionJpaRepository = optionJpaRepository;
        this.entityManager = entityManager;
    }

    @Override
    public Optional<Option> findById(Long id) {
        return optionJpaRepository.findById(id).map(OptionEntityMapper::toDomain);
    }

    @Override
    public Option save(Option option) {
        ProductEntity productEntityProxy = entityManager.getReference(ProductEntity.class, option.getProductId());
        var entity = OptionEntityMapper.toEntity(option, productEntityProxy);
        var savedEntity = optionJpaRepository.save(entity);
        return OptionEntityMapper.toDomain(savedEntity);
    }
} 