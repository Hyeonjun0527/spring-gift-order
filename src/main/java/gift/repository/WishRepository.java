package gift.repository;

import gift.entity.Member;
import gift.entity.Wish;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishRepository extends JpaRepository<Wish, Long> {

    // 특정 회원의 모든 위시리스트 (최신순 정렬)
    Page<Wish> findByMember(Member member, Pageable pageable);

    // 특정 회원이 특정 상품을 찜했는지 여부
    boolean existsByMemberIdAndProductId(Long memberId, Long productId);
}