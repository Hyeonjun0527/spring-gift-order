package gift.wish.adapter.web.mapper;

import gift.wish.application.port.in.dto.WishResponse;
import gift.wish.domain.model.Wish;
import org.springframework.stereotype.Component;

@Component
public class WishWebMapper {
    private static WishResponse toResponse(Long wishId, ProductInfo productInfo, int quantity) {
        return new WishResponse(wishId, productInfo.id(), productInfo.name(),
                               productInfo.price(), productInfo.imageUrl(), quantity);
    }

    public static WishResponse toResponse(Wish wish) {
        var productInfo = new ProductInfo(
                wish.getProduct().getId(),
                wish.getProduct().getName(),
                wish.getProduct().getPrice(),
                wish.getProduct().getImageUrl()
        );
        return toResponse(wish.getId(), productInfo, wish.getQuantity());
    }

    private record ProductInfo(Long id, String name, int price, String imageUrl) {}
}
