package gift.wish.application.usecase;

import gift.common.exception.UnauthorizedException;
import gift.member.domain.model.Member;
import gift.member.domain.port.out.MemberRepository;
import gift.product.domain.model.Product;
import gift.product.domain.port.out.ProductRepository;
import gift.wish.application.port.in.WishUseCase;
import gift.wish.application.port.in.dto.WishAddRequest;
import gift.wish.domain.model.Wish;
import gift.wish.domain.port.out.WishRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WishInteractor implements WishUseCase {

    private final WishRepository wishRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    public WishInteractor(WishRepository wishRepository, MemberRepository memberRepository, ProductRepository productRepository) {
        this.wishRepository = wishRepository;
        this.memberRepository = memberRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Wish> getWishes(Long memberId, Pageable pageable) {
        return wishRepository.findByMemberId(memberId, pageable);
    }

    @Override
    public Wish addWish(WishAddRequest request, Long memberId) {
        return wishRepository.findByMemberIdAndProductId(memberId, request.productId())
                .map(existingWish -> {
                    existingWish.updateQuantity(existingWish.getQuantity() + request.quantity());
                    return wishRepository.save(existingWish);
                })
                .orElseGet(() -> {
                    Member member = memberRepository.findById(memberId)
                            .orElseThrow(() -> new IllegalArgumentException("회원을 찾을 수 없습니다."));
                    Product product = productRepository.findById(request.productId())
                            .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
                    Wish newWish = Wish.of(null, member, product, request.quantity());
                    return wishRepository.save(newWish);
                });
    }

    @Override
    public Wish updateWishQuantity(Long wishId, int quantity, Long memberId) {
        Wish wish = wishRepository.findById(wishId)
                .orElseThrow(() -> new IllegalArgumentException("위시리스트를 찾을 수 없습니다."));
        if (!wish.getMember().id().equals(memberId)) {
            throw new UnauthorizedException("위시리스트를 수정할 수 없습니다.");
        }
        wish.updateQuantity(quantity);
        return wishRepository.save(wish);
    }

    @Override
    public void deleteWish(Long wishId, Long memberId) {
        Wish wish = wishRepository.findById(wishId)
                .orElseThrow(() -> new IllegalArgumentException("위시리스트를 찾을 수 없습니다."));
        if (!wish.getMember().id().equals(memberId)) {
            throw new UnauthorizedException("위시리스트를 삭제할 수 없습니다.");
        }
        wishRepository.deleteById(wishId);
    }
}
