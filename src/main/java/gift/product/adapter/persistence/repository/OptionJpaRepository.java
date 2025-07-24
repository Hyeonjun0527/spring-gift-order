package gift.product.adapter.persistence.repository;

import gift.product.adapter.persistence.entity.OptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OptionJpaRepository extends JpaRepository<OptionEntity, Long> {

    List<OptionEntity> findByProductId(Long productId);

}
