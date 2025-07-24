package gift.product.adapter.web.mapper;

import gift.product.application.port.in.dto.OptionResponse;
import gift.product.application.port.in.dto.ProductResponse;
import gift.product.domain.model.Product;

import java.util.stream.Collectors;

public class ProductWebMapper {

    public static ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getImageUrl(),
                product.getOptions().stream()
                        .map(option -> new OptionResponse(option.getId(), option.getName(), option.getQuantity()))
                        .collect(Collectors.toList())
        );
    }
} 