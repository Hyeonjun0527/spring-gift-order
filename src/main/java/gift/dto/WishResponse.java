package gift.dto;

public record WishResponse(
    Long id,
    Long productId,
    String name,
    Integer quantity,
    Long memberId
) {

}
