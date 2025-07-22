package gift.repository;

import gift.entity.Option;
import gift.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OptionRepository extends JpaRepository<Option, Long> {
    boolean existsByProductAndName(Product product, String name);

    boolean existsByProductIdAndName(Long productId, String name);

    List<Option> findAllByProductId(Long productId);

    void deleteAllByProduct(Product product);

    Optional<Option> findByIdAndProductId(Long optionId, Long productId);

    int countByProduct(Product product);
}
