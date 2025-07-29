package gift.product.adapter.persistence.adapter;

import gift.product.adapter.persistence.mapper.OptionEntityMapper;
import gift.product.adapter.persistence.repository.OptionJpaRepository;
import gift.product.adapter.persistence.repository.ProductJpaRepository;
import gift.product.domain.model.Option;
import gift.product.domain.port.out.OptionRepository;
import gift.product.exception.ProductException;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class OptionPersistenceAdapter implements OptionRepository {

    private final OptionJpaRepository optionJpaRepository;
    private final ProductJpaRepository productJpaRepository;

    public OptionPersistenceAdapter(OptionJpaRepository optionJpaRepository, ProductJpaRepository productJpaRepository) {
        this.optionJpaRepository = optionJpaRepository;
        this.productJpaRepository = productJpaRepository;
    }

    @Override
    public Optional<Option> findById(Long id) {
        return optionJpaRepository.findById(id).map(OptionEntityMapper::toDomain);
    }

    @Override
    public Option save(Option option) {
        var productEntity = productJpaRepository.findById(option.getProductId())
                .orElseThrow(() -> new ProductException("그런 프로덕트가 없습니다 : " + option.getProductId()));
        var entity = OptionEntityMapper.toEntity(option, productEntity);
        var savedEntity = optionJpaRepository.save(entity);
        return OptionEntityMapper.toDomain(savedEntity);
    }
} 