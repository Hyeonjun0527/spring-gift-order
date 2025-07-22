package gift.entity;

import jakarta.persistence.*;

@Entity
public class ProductOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(nullable = false)
    private int quantity;

    protected ProductOption() {}

    public ProductOption(Product product, String name, int quantity) {
        this.product = product;
        this.name = name;
        this.quantity = quantity;
    }

    public void subtract(int amount) {
        if (amount < 1) throw new IllegalArgumentException("차감 수량은 1 이상이어야 합니다.");
        if (this.quantity < amount) throw new IllegalArgumentException("수량이 부족합니다.");
        this.quantity -= amount;
    }

    public Long getId() {return id;}
    public void setId(Long id) {this.id = id;}
    public Product getProduct() {return product;}
    public String getName() {return name;}
    public int getQuantity() {return quantity;}

}