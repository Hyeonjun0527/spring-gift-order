package gift.product.adapter.persistence.adapter;

import gift.common.annotation.Adapter;
import gift.product.adapter.persistence.entity.ProductEntity;
import gift.product.adapter.persistence.mapper.ProductEntityMapper;
import gift.product.adapter.persistence.repository.ProductJpaRepository;
import gift.product.domain.model.Product;
import gift.product.domain.port.out.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

@Adapter
public class ProductPersistenceAdapter implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;

    public ProductPersistenceAdapter(ProductJpaRepository productJpaRepository) {
        this.productJpaRepository = productJpaRepository;
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
    public Product save(Product product) {
        ProductEntity entity = ProductEntityMapper.toEntity(product);
        ProductEntity save = productJpaRepository.save(entity);
        return ProductEntityMapper.toDomain(save);
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
