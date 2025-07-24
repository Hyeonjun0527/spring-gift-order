package gift.wish.application.port.in;

import gift.wish.application.port.in.dto.WishAddRequest;
import gift.wish.domain.model.Wish;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface WishUseCase {
    Page<Wish> getWishes(Long memberId, Pageable pageable);

    Wish addWish(WishAddRequest request, Long memberId);

    Wish updateWishQuantity(Long wishId, int quantity, Long memberId);

    void deleteWish(Long wishId, Long memberId);
} 