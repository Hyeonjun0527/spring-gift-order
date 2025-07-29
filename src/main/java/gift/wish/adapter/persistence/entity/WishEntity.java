package gift.wish.adapter.persistence.entity;

import gift.member.adapter.persistence.entity.MemberEntity;
import gift.product.adapter.persistence.entity.ProductEntity;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "wish",
        uniqueConstraints = @UniqueConstraint(columnNames = {"member_id", "option_id"})
)
public class WishEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Column(name = "option_id", nullable = false)
    private Long optionId;

    @Column(name = "option_name", nullable = false)
    private String optionName;

    @Column(nullable = false)
    private int quantity;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected WishEntity() {}

    private WishEntity(Long id, MemberEntity member, ProductEntity product, Long optionId, String optionName, int quantity) {
        this.id = id;
        this.member = member;
        this.product = product;
        this.optionId = optionId;
        this.optionName = optionName;
        this.quantity = quantity;
    }

    public static WishEntity create(MemberEntity member, ProductEntity product, Long optionId, String optionName, int quantity) {
        return new WishEntity(null, member, product, optionId, optionName, quantity);
    }

    public static WishEntity of(Long id, MemberEntity member, ProductEntity product, Long optionId, String optionName, int quantity) {
        return new WishEntity(id, member, product, optionId, optionName, quantity);
    }

    public Long getId() {
        return id;
    }

    private void setId(Long id) {
        this.id = id;
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

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
        
    public void changeMember(MemberEntity newMember) {

        if (this.member != null) {
            this.member.getWishes().remove(this);
        }
        this.member = newMember;

        
    
        if (newMember != null && !newMember.getWishes().contains(this)) {
            newMember.getWishes().add(this);
        }
    }

   public void changeProduct(ProductEntity newProduct) {

        if (this.product != null) {
            this.product.getWishes().remove(this);
        }

        this.product = newProduct;

        if (newProduct != null && !newProduct.getWishes().contains(this)) {
            newProduct.getWishes().add(this);
        }
    }

    public MemberEntity getMember() { return member; }

    public ProductEntity getProduct() { return product; }

} 