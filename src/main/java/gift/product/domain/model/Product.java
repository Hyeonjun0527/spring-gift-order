package gift.product.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Product {

    private final Long id;
    private String name;
    private int price;
    private String imageUrl;
    private List<Option> options;

    private Product(Long id, String name, int price, String imageUrl, List<Option> options) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.options = new ArrayList<>(options);
    }

    public static Product create(Long id, String name, int price, String imageUrl, List<Option> options) {
        validateOptions(options);
        return new Product(id, name, price, imageUrl, new ArrayList<>(options));
    }

    public static Product of(Long id, String name, int price, String imageUrl, List<Option> options) {
        return new Product(id, name, price, imageUrl, options);
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

    private static void validateOptions(List<Option> options) {
        if (options == null || options.isEmpty()) {
            throw new IllegalArgumentException("상품에는 반드시 하나 이상의 옵션이 있어야 합니다.");
        }

        Set<String> distinctNames = options.stream()
                .map(Option::getName)
                .collect(Collectors.toSet());
        if (distinctNames.size() < options.size()) {
            throw new IllegalArgumentException("옵션 이름은 중복될 수 없습니다.");
        }
    }

    public List<Option> getOptions() {
        return options;
    }

    public void updateInfo(String name, int price, String imageUrl, List<Option> options) {
        validateOptions(options);
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.options = new ArrayList<>(options);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}