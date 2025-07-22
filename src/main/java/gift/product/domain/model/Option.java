package gift.product.domain.model;

import gift.product.exception.ProductException;

public class Option {
    private Long id;
    private Long productId;
    private String name;
    private int quantity;

    private Option(Long id, Long productId, String name, int quantity) {
        this.id = id;
        this.productId = productId;
        this.name = name;
        this.quantity = quantity;
    }
    public static Option create(Long id, Long productId, String name, int quantity) {
        return new Option(id, productId, name, quantity);
    }
    public static Option of(Long id, Long productId, String name, int quantity) {

        return new Option(id, productId, name, quantity);
    }

    public Long getId() {
        return id;
    }

    public Long getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void subtractQuantity(int cnt) {
        if (this.quantity < cnt) {
            throw new ProductException("재고가 부족합니다.");
        }
        this.quantity-= cnt;
    }
}
