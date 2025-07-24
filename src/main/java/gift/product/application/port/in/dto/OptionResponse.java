package gift.product.application.port.in.dto;

public record OptionResponse(
        Long id,
        String name,
        int quantity
){
    public static OptionResponse of(Long id, String name, int quantity) {
        return new OptionResponse(id, name, quantity);
    }
}
