package gift.dto;

import gift.entity.Wish;

public class WishResponseDto {
    private Long id;
    private ProductResponseDto product;
    private Integer quantity;

    public WishResponseDto() {}

    public WishResponseDto(Wish wish, ProductResponseDto product) {
        this.id = wish.getId();
        this.product = product;
        this.quantity = wish.getQuantity();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ProductResponseDto getProduct() { return product; }
    public void setProduct(ProductResponseDto product) { this.product = product; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
} 