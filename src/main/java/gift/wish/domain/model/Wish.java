package gift.wish.domain.model;

import gift.member.domain.model.Member;
import gift.product.domain.model.Product;

public class Wish {
    private final Long id;
    private final Member member;
    private final Product product;
    private final Long optionId;
    private final String optionName;
    private int quantity;

    private Wish(Long id, Member member, Product product, Long optionId, String optionName, int quantity) {
        this.id = id;
        this.member = member;
        this.product = product;
        this.optionId = optionId;
        this.optionName = optionName;
        this.quantity = quantity;
    }

    public static Wish of(Long id, Member member, Product product, Long optionId, String optionName, int quantity) {
        return new Wish(id, member, product, optionId, optionName, quantity);
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public Product getProduct() {
        return product;
    }

    public Long getOptionId() {
        return optionId;
    }

    public String getOptionName() {
        return optionName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void updateQuantity(int quantity) {
        this.quantity = quantity;
    }
}
