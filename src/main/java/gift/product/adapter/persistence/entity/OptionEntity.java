package gift.product.adapter.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "option",
    uniqueConstraints = {
        @UniqueConstraint(
                name = "product_id_name_unique",
                columnNames = {"product_id", "name"}
        )
    })
public class OptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private int quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    protected OptionEntity() {
    }

    private OptionEntity(Long id, String name, int quantity, ProductEntity product) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.product = product;
    }

    public static OptionEntity of(Long id, String name, int quantity, ProductEntity product) {
        return new OptionEntity(id, name, quantity, product);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public ProductEntity getProduct() {
        return product;
    }

}
