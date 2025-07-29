package gift.order.adapter.persistence.entity;

import gift.member.adapter.persistence.entity.MemberEntity;
import gift.product.adapter.persistence.entity.OptionEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "`Order`")
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private MemberEntity member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id", nullable = false)
    private OptionEntity option;

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private String optionName;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int quantity;

    private String message;

    @Column(nullable = false)
    private LocalDateTime orderDateTime;

    public OrderEntity() {
    }

    public OrderEntity(Long id, MemberEntity member, OptionEntity option, String productName, String optionName, int price, int quantity, String message, LocalDateTime orderDateTime) {
        this.id = id;
        this.member = member;
        this.option = option;
        this.productName = productName;
        this.optionName = optionName;
        this.price = price;
        this.quantity = quantity;
        this.message = message;
        this.orderDateTime = orderDateTime;
    }

    public Long getId() {
        return id;
    }

    public MemberEntity getMember() {
        return member;
    }

    public OptionEntity getOption() {
        return option;
    }

    public String getProductName() {
        return productName;
    }

    public String getOptionName() {
        return optionName;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getOrderDateTime() {
        return orderDateTime;
    }
} 