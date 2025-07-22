package gift.repository;

import gift.entity.ProductOption;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductOptionRepository extends JpaRepository<ProductOption, Long> {
    boolean existsByProductIdAndName(Long productId, String name);
    Page<ProductOption> findByProductId(Long productId, Pageable pageable);
} 