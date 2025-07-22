package gift.product.adapter.persistence.entity;

import gift.wish.adapter.persistence.entity.WishEntity;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false, length = 255)
    private String imageUrl;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
    private List<WishEntity> wishes = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, orphanRemoval = true)
    private List<OptionEntity> options = new ArrayList<>();

    protected ProductEntity() {
    }

    private ProductEntity(Long id, String name, int price, String imageUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public static ProductEntity of(String name, int price, String imageUrl) {
        return new ProductEntity(null, name, price, imageUrl);
    }

    public static ProductEntity of(Long id, String name, int price, String imageUrl) {
        return new ProductEntity(id, name, price, imageUrl);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public List<WishEntity> getWishes() {
        return wishes;
    }

    public List<OptionEntity> getOptions() {
        return options;
    }
} 