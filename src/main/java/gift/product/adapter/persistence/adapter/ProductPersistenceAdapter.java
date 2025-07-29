package gift.product.adapter.persistence.adapter;

import gift.common.annotation.Adapter;
import gift.product.adapter.persistence.entity.ProductEntity;
import gift.product.adapter.persistence.mapper.OptionEntityMapper;
import gift.product.adapter.persistence.mapper.ProductEntityMapper;
import gift.product.adapter.persistence.repository.OptionJpaRepository;
import gift.product.adapter.persistence.repository.ProductJpaRepository;
import gift.product.domain.model.Option;
import gift.product.domain.model.Product;
import gift.product.domain.port.out.ProductRepository;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Adapter
public class ProductPersistenceAdapter implements ProductRepository {

    private static final Logger log = LoggerFactory.getLogger(ProductPersistenceAdapter.class);
    private final EntityManager entityManager;

    private final ProductJpaRepository productJpaRepository;
    private final OptionJpaRepository optionJpaRepository;

    public ProductPersistenceAdapter(ProductJpaRepository productJpaRepository, OptionJpaRepository optionJpaRepository, EntityManager entityManager) {
        this.productJpaRepository = productJpaRepository;
        this.optionJpaRepository = optionJpaRepository;
        this.entityManager = entityManager;
    }

    @Override
    public Page<Product> findAll(Pageable pageable) {
        return productJpaRepository.findAll(pageable)
                .map(ProductEntityMapper::toDomain);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return productJpaRepository.findById(id)
                .map(ProductEntityMapper::toDomain);
    }

    @Override
    public Optional<Option> findOptionById(Long optionId) {
        return optionJpaRepository.findById(optionId)
                .map(OptionEntityMapper::toDomain);
    }

    @Override
    public Product save(Product product) {
        ProductEntity entity = ProductEntityMapper.toEntity(product);
        log.info("6 save productEntity: {}", entity);
        ProductEntity mergedEntity = entityManager.merge(entity);
        return ProductEntityMapper.toDomain(mergedEntity);
    }

    @Override
    public void deleteById(Long id) {
        productJpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return productJpaRepository.existsById(id);
    }
} 