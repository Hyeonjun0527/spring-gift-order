package gift.wish.domain.port.out;

import gift.wish.domain.model.Wish;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface WishRepository {
    Page<Wish> findByMemberId(Long memberId, Pageable pageable);

    Wish save(Wish wish);

    Optional<Wish> findById(Long id);

    void deleteById(Long id);

    Optional<Wish> findByMemberIdAndProductId(Long memberId, Long productId);
}
