package gift.service;

import gift.dto.ProductResponseDto;
import gift.dto.WishRequestDto;
import gift.dto.WishResponseDto;
import gift.entity.Member;
import gift.entity.Product;
import gift.entity.Wish;
import gift.exception.ForbiddenAccessException;
import gift.repository.ProductRepository;
import gift.repository.WishRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WishService {

    private final WishRepository wishRepository;
    private final ProductRepository productRepository;

    public WishService(WishRepository wishRepository, ProductRepository productRepository) {
        this.wishRepository = wishRepository;
        this.productRepository = productRepository;
    }

    // 위시리스트 추가
    @Transactional
    public WishResponseDto addWish(Member member, WishRequestDto requestDto) {
        // 상품 존재 여부 확인
        Product product = productRepository.findById(requestDto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

        // 이미 위시리스트에 있는 상품인지 확인
        if (wishRepository.existsByMemberIdAndProductId(member.getId(), requestDto.getProductId())) {
            throw new IllegalArgumentException("이미 위시리스트에 추가된 상품입니다.");
        }

        // 위시리스트에 상품 추가
        Wish wish = new Wish(member,product, requestDto.getQuantity());
        Wish savedWish = wishRepository.save(wish);

        return new WishResponseDto(savedWish, new ProductResponseDto(product));
    }

    // 위시리스트 조회
    @Transactional(readOnly = true)
    public Page<WishResponseDto> getWishesByMember(Member member, Pageable pageable) {
        Page<Wish> wishes = wishRepository.findByMember(member, pageable);
        return wishes.map(wish -> new WishResponseDto(wish, new ProductResponseDto(wish.getProduct())));
    }

    // 위시리스트 수량 변경
    @Transactional
    public WishResponseDto updateWishQuantity(Member member, Long wishId, Integer quantity) {
        // 위시리스트 항목 조회
        Wish wish = wishRepository.findById(wishId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

        // 해당 ID의 상품이 현재 로그인한 사용자 건지 검증 
        if (!wish.getMember().getId().equals(member.getId())) {
            throw new ForbiddenAccessException("권한이 없습니다."); 
        }

        // Dirty Checking으로 수량 업데이트
        wish.setQuantity(quantity);

        return new WishResponseDto(wish, new ProductResponseDto(wish.getProduct()));
    }

    // 위시리스트 삭제
    @Transactional
    public void deleteWish(Member member, Long wishId) {
        Wish wish = wishRepository.findById(wishId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

        if (!wish.getMember().getId().equals(member.getId())) {
            throw new ForbiddenAccessException("권한이 없습니다.");
        }

        wishRepository.deleteById(wishId);
    }
} 