package gift.dto;

import gift.entity.ProductOption;

public class ProductOptionResponseDto {
    private Long id;
    private String name;
    private int quantity;

    public ProductOptionResponseDto() {}

    public ProductOptionResponseDto(Long id, String name, int quantity) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
    }

    public ProductOptionResponseDto(ProductOption option) {
        this.id = option.getId();
        this.name = option.getName();
        this.quantity = option.getQuantity();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}