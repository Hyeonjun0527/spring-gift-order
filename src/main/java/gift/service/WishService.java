package gift.service;

import gift.dto.ProductResponseDto;
import gift.dto.WishCreateResponseDto;
import gift.dto.WishResponseDto;
import gift.entity.Member;
import gift.entity.Product;
import gift.entity.Wish;
import gift.exception.MemberNotFoundException;
import gift.exception.ProductNotExistException;
import gift.exception.WishAlreadyExistException;
import gift.exception.WishNotExistException;
import gift.repository.MemberRepository;
import gift.repository.ProductRepository;
import gift.repository.WishRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WishService {

    private final WishRepository wishRepository;
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;

    public WishService(WishRepository wishRepository,
                       ProductRepository productRepository,
                       MemberRepository memberRepository) {
        this.wishRepository = wishRepository;
        this.productRepository = productRepository;
        this.memberRepository = memberRepository;
    }

    public List<WishResponseDto> getWishlist(Long memberId, Pageable pageable) {

        List<Wish> wishes = wishRepository.findAllByMemberId(memberId, pageable);

        return wishes.stream()
                .map(wish -> {
                    Product product = productRepository.findById(wish.getProduct().getId())
                            .orElseThrow(() -> new ProductNotExistException(wish.getProduct().getId()));
                    ProductResponseDto productResponseDto = new ProductResponseDto(
                            product.getId(),
                            product.getName(),
                            product.getPrice(),
                            product.getImageUrl()
                    );
                    return new WishResponseDto(
                            wish.getId(),
                            productResponseDto
                    );
                })
                .collect(Collectors.toList());
    }

    public WishCreateResponseDto add(Long memberId, Long productId) {

        if (wishRepository.existsByMemberIdAndProductId(memberId, productId)) {
            throw new WishAlreadyExistException(productId);
        }

        if (!productRepository.existsById(productId)) {
            throw new ProductNotExistException(productId);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("id", memberId.toString()));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotExistException(productId));

        Wish wish = new Wish(member, product);
        wishRepository.save(wish);

        return new WishCreateResponseDto(wish.getId(), wish.getMember().getId(), wish.getProduct().getId());
    }

    public void remove(Long wishId) {
        Wish wish = wishRepository.findById(wishId)
                .orElseThrow(() -> new WishNotExistException(wishId));

        wishRepository.delete(wish);
    }
}