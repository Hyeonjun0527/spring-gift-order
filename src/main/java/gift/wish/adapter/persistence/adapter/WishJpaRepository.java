package gift.wish.adapter.persistence.adapter;

import gift.wish.adapter.persistence.entity.WishEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WishJpaRepository extends JpaRepository<WishEntity, Long> {
    Page<WishEntity> findByMemberId(Long memberId, Pageable pageable);
    Optional<WishEntity> findByMemberIdAndProductId(Long memberId, Long productId);
} 