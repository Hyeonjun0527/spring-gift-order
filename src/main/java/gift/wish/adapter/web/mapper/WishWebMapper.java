package gift.wish.adapter.web.mapper;

import gift.wish.application.port.in.dto.WishResponse;
import gift.wish.domain.model.Wish;
import org.springframework.stereotype.Component;

@Component
public class WishWebMapper {
    private static WishResponse toResponse(Long wishId, ProductInfo productInfo, OptionInfo optionInfo, int quantity) {
        return new WishResponse(wishId, productInfo.id(), optionInfo.id(), productInfo.name(),
                optionInfo.name(),
                productInfo.price(), productInfo.imageUrl(), quantity);
    }

    public static WishResponse toResponse(Wish wish) {
        var productInfo = new ProductInfo(
                wish.getProduct().getId(),
                wish.getProduct().getName(),
                wish.getProduct().getPrice(),
                wish.getProduct().getImageUrl()
        );
        var optionInfo = new OptionInfo(
                wish.getOptionId(),
                wish.getOptionName()
        );
        return toResponse(wish.getId(), productInfo, optionInfo,  wish.getQuantity());
    }

    private record ProductInfo(Long id, String name, int price, String imageUrl) {}
    private record OptionInfo(Long id, String name) {};
}
