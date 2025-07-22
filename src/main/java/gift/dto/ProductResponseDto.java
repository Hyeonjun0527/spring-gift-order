package gift.dto;

import gift.entity.Product;

public class ProductResponseDto {
    private Long id;
    private String name;
    private int price;
    private String imageUrl;

    public ProductResponseDto(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.price = product.getPrice();
        this.imageUrl = product.getImageUrl();
    }

    public ProductResponseDto() {}

    public Long getId() { return id; }
    public String getName() { return name; }
    public int getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
}
