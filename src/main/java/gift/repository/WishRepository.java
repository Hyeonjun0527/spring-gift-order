package gift.repository;

import gift.entity.Wish;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishRepository extends JpaRepository<Wish, Long> {

    List<Wish> findByMemberId(Long memberId);

    boolean existsByMemberIdAndProductId(Long memberId,  Long productId);

    List<Wish> findAllByMemberId(Long memberId, Pageable pageable);
}