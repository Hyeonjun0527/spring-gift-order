package gift.product.domain.port.out;

import gift.product.domain.model.Option;
import gift.product.domain.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ProductRepository {

    Page<Product> findAll(Pageable pageable);

    Optional<Product> findById(Long id);

    Optional<Option> findOptionById(Long optionId);

    Product save(Product product);

    void deleteById(Long id);

    boolean existsById(Long id);
} 