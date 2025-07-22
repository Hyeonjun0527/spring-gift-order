package gift.dto;

import java.util.List;

public record ProductOptionResponse(
    Long id,
    String name,
    Integer price,
    String imageUrl,
    List<OptionResponse> options
) {

}
