package gift.dto;

public record ProductRequest(
    String name,
    Integer price,
    String imageUrl
) {

}