package gift.product.adapter.persistence.entity;

import gift.wish.adapter.persistence.entity.WishEntity;
import jakarta.persistence.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@Table(name = "product")
public class ProductEntity {

    private static final Logger log = LoggerFactory.getLogger(ProductEntity.class);

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

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OptionEntity> options = new ArrayList<>();

    protected ProductEntity() {
    }

    private ProductEntity(Long id, String name, int price, String imageUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public static ProductEntity create(String name, int price, String imageUrl, List<OptionEntity> options) {
        ProductEntity entity = new ProductEntity(null, name, price, imageUrl);
        entity.setOptions(options);
        return entity;
    }

    public static ProductEntity of(Long id, String name, int price, String imageUrl, List<OptionEntity> options) {
        ProductEntity entity = new ProductEntity(id, name, price, imageUrl);
        entity.setOptions(options);
        return entity;
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

    public void updateInfo(String name, int price, String imageUrl, List<OptionEntity> newOptions) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        setOptions(newOptions);
    }

    public void changeName(String name) {
        this.name = name;
    }

    // (비교용) 작동하지 않는 옵션 설정 방식
    public void setOptionsByClearAndAdd(List<OptionEntity> newOptions) {
        this.options.clear();
        for (OptionEntity incoming : newOptions) {
            addOption(incoming);
        }
    }

    // !! 테스트용: 이런 방식은 예외를 유발할 수 있습니다. !!
    public void replaceOptionsForTest(List<OptionEntity> newOptions) {
        this.options = newOptions;
        for (OptionEntity option : newOptions) {
            option.setProduct(this);
        }
    }

    // 제대로 된 옵션 설정 방식
    public void setOptions(List<OptionEntity> newOptions) {
        Map<Long, OptionEntity> currentOptionsMap = this.options.stream()
                .filter(option -> option.getId() != null)
                .collect(Collectors.toMap(OptionEntity::getId, option -> option));

        for (OptionEntity incoming : newOptions) {
            if (incoming.getId() != null) {
                OptionEntity managed = currentOptionsMap.remove(incoming.getId());
                if (managed != null) {
                    managed.updateOption(incoming.getName(), incoming.getQuantity());
                } else {
                    addOption(incoming);
                }
            } else {
                addOption(incoming);
            }
        }

        for (OptionEntity orphan : currentOptionsMap.values()) {
            removeOption(orphan);
        }
    }

    public void addOption(OptionEntity option) {
        option.setProduct(this);
        this.options.add(option);
    }

    private void removeOption(OptionEntity option) {
        this.options.remove(option);
    }
    @Override
    public String toString() {
        return "ProductEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", imageUrl='" + imageUrl + '\'' +
                ", wishes=" + wishes +
                ", options=" + options +
                '}';
    }
}