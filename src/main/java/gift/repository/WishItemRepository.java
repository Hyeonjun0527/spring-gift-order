package gift.repository;

import gift.entity.WishItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishItemRepository extends JpaRepository<WishItem, Long> {

    Page<WishItem> findAllByMemberId(Long memberId, Pageable pageable);
}
