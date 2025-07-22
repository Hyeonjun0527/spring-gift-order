package gift.dto;

import gift.validation.ValidProduct;

@ValidProduct
public class ProductRequestDto {
    private String name;
    private int price;
    private String imageUrl;

    public ProductRequestDto() {}

    public ProductRequestDto(String name, int price, String imageUrl) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public String getName() { return name; }
    public int getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }

    public void setName(String name) { this.name = name; }
    public void setPrice(int price) { this.price = price; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl;}
}
