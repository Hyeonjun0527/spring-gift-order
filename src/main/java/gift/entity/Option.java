package gift.entity;

import gift.exception.InvalidQuantityException;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private String name;
    private Integer quantity;

    protected Option() {
    }

    public Option(Long id, Product product, String name, Integer quantity) {
        this.id = id;
        this.product = product;
        this.name = name;
        this.quantity = quantity;
    }

    public Option(Product product, String name, Integer quantity) {
        this(null, product, name, quantity);
    }

    public void subtractOptionNum(Integer quantity) {
        if (quantity < 0) {
            throw new InvalidQuantityException("감소 수량은 0 이상이어야 합니다.");
        }
        if (this.quantity - quantity < 0) {
            throw new InvalidQuantityException("옵션 수량이 부족합니다.");
        }
        this.quantity -= quantity;
    }

    public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public String getName() {
        return name;
    }

    public Integer getQuantity() {
        return quantity;
    }

}
