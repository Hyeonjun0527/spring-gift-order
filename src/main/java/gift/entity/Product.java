package gift.entity;

import gift.exception.InvalidOptionNameException;
import gift.exception.InvalidQuantityException;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Integer price;
    private String imageUrl;

    @OneToMany(mappedBy = "product")
    private List<WishItem> wishItems = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    private List<Option> options = new ArrayList<>();

    protected Product() {
    }

    public Product(Long id, String name, Integer price, String imageUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        addDefaultOption();
    }

    public Product(String name, Integer price, String imageUrl) {
        this(null, name, price, imageUrl);
    }

    public void addOption(String name, Integer quantity) {
        if (options.stream().anyMatch(option -> option.getName().equals(name))) {
            throw new InvalidOptionNameException("동일한 옵션 이름이 이미 존재합니다.");
        }
        if (name.length() > 50) {
            throw new InvalidOptionNameException("옵션 이름은 최대 50자까지 가능합니다.");
        }
        if (!isValidOptionName(name)) {
            throw new InvalidOptionNameException("허용되지 않은 특수 문자가 포함되었습니다.");
        }
        if (quantity < 1 || quantity >= 100000000) {
            throw new InvalidQuantityException("옵션 수량은 1이상 1억 미만이어야 합니다.");
        }
        options.add(new Option(this, name, quantity));
    }

    private boolean isValidOptionName(String name) {
        return name.matches("^[a-zA-Z0-9가-힣\\s\\(\\)\\[\\]\\+\\-&_/]*$");
    }

    private void addDefaultOption() {
        if (options.isEmpty()) {
            addOption("Default Option", 123);
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public List<WishItem> getWishItems() {
        return wishItems;
    }

    public List<Option> getOptions() {
        return options;
    }

    @Override
    public String toString() {
        return "Product(" + name + ") - price: " + price;
    }


}