package gift.product.application.port.in.dto;

import java.util.List;

public record ProductResponse(
        Long id,
        String name,
        int price,
        String imageUrl,
        List<OptionResponse> options
) {
}