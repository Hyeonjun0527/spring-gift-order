package gift.dto;

public record ProductResponse(
    Long id,
    String name,
    Integer price,
    String imageUrl
) {

}