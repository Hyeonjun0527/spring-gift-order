package gift.wish.domain.model;

import gift.member.domain.model.Member;
import gift.product.domain.model.Product;

public class Wish {
    private final Long id;
    private final Member member;
    private final Product product;
    private int quantity;

    private Wish(Long id, Member member, Product product, int quantity) {
        this.id = id;
        this.member = member;
        this.product = product;
        this.quantity = quantity;
    }

    public static Wish of(Long id, Member member, Product product, int quantity) {
        return new Wish(id, member, product, quantity);
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

    public int getQuantity() {
        return quantity;
    }

    public void updateQuantity(int quantity) {
        this.quantity = quantity;
    }
}
