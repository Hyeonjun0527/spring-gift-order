package gift.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class WishRequestDto {
    
    @NotNull(message = "상품 ID는 필수입니다.")
    private Long productId;
    
    @Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
    private Integer quantity = 1; // 기본값 1로 설정

    public WishRequestDto() {}

    public WishRequestDto(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity != null ? quantity : 1;
    }

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { 
        this.quantity = quantity != null ? quantity : 1; 
    }
} 