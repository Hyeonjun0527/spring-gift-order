package gift.service;

import gift.dto.PaginationResponse;
import gift.dto.WishRequest;
import gift.dto.WishResponse;
import gift.entity.Member;
import gift.entity.Product;
import gift.entity.WishItem;
import gift.exception.ProductNotFoundException;
import gift.repository.ProductRepository;
import gift.repository.WishItemRepository;
import gift.validation.ValidationUtil;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class WishService {

    private final WishItemRepository wishItemRepository;
    private final ProductRepository productRepository;

    public WishService(WishItemRepository wishItemRepository, ProductRepository productRepository) {
        this.wishItemRepository = wishItemRepository;
        this.productRepository = productRepository;
    }

    public List<WishResponse> getWishlist(Member member) {
        List<WishItem> wishItems = member.getWishItems();
        return wishItems.stream()
            .map(item -> new WishResponse(
                item.getId(),
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                member.getId()
            ))
            .collect(Collectors.toList());
    }

    public PaginationResponse<WishResponse> getWishlistPaged(Long memberId, Pageable pageable) {
        Page<WishItem> page = wishItemRepository.findAllByMemberId(memberId, pageable);
        List<WishResponse> content = page.getContent().stream()
            .map(wishItem -> new WishResponse(
                wishItem.getId(),
                wishItem.getProduct().getId(),
                wishItem.getProduct().getName(),
                wishItem.getQuantity(),
                wishItem.getMember().getId()
            ))
            .collect(Collectors.toList());

        return new PaginationResponse<>(
            content,
            page.getTotalPages(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements()
        );
    }

    @Transactional
    public WishResponse addToWishlist(WishRequest request, Member member) {
        ValidationUtil.validateWishRequestAndMember(request, member);

        Product product = productRepository.findById(request.productId())
            .orElseThrow(() -> new ProductNotFoundException(
                "Product(id: " + request.productId() + ") not found"));
        WishItem wishItem = new WishItem(
            null,
            product,
            request.quantity(),
            member
        );
        WishItem savedItem = wishItemRepository.save(wishItem);

        return new WishResponse(
            savedItem.getId(),
            savedItem.getProduct().getId(),
            savedItem.getProduct().getName(),
            savedItem.getQuantity(),
            member.getId()
        );
    }

    @Transactional
    public void removeFromWishlist(Long wishId, Member member) {
        ValidationUtil.validatePIDAndMember(wishId, member);

        wishItemRepository.deleteById(wishId);
    }

}
