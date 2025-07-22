package gift.repository;

import static org.assertj.core.api.Assertions.assertThat;

import gift.dto.WishResponse;
import gift.entity.Member;
import gift.entity.Product;
import gift.entity.WishItem;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@ActiveProfiles("test")
public class WishItemRepositoryTest {

    @Autowired
    private WishItemRepository wishItemRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    private Member member;
    private Product product;
    private WishItem wishItem;

    @BeforeEach
    void setUp() {
        member = new Member("test1@test.com", "password123", "USER");
        member = memberRepository.save(member);

        product = new Product("Test Product", 1000, "http://test.com");
        product = productRepository.save(product);

        wishItem = new WishItem(product, 2, member);
        wishItem = wishItemRepository.save(wishItem);
    }

    @Test
    void save() {
        WishItem savedWishItem = wishItemRepository.save(wishItem);

        assertThat(savedWishItem.getId()).isNotNull();
        assertThat(savedWishItem.getMember()).isEqualTo(member);
        assertThat(savedWishItem.getProduct()).isEqualTo(product);
        assertThat(savedWishItem.getQuantity()).isEqualTo(2);
    }

    @Test
    void deleteByIdAndMemberId() {
        WishItem savedWishItem = wishItemRepository.save(wishItem);
        wishItemRepository.deleteById(savedWishItem.getId());
        List<WishItem> wishItems = member.getWishItems();

        assertThat(wishItems).isEmpty();
    }

    @Test
    void findAllPaged() {
        for (int i = 2; i <= 15; i++) {
            WishItem wishItem = new WishItem(product, i, member);
            wishItemRepository.save(wishItem);
        }

        Pageable pageable = PageRequest.of(0, 5);
        Page<WishResponse> page = wishItemRepository.findAll(pageable)
            .map(wishItem -> new WishResponse(
                wishItem.getId(),
                wishItem.getProduct().getId(),
                wishItem.getProduct().getName(),
                wishItem.getQuantity(),
                wishItem.getMember().getId()
            ));

        assertThat(page.getContent()).hasSize(5);
        assertThat(page.getTotalPages()).isEqualTo(3);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getContent().getFirst().name()).startsWith("Test Product");
    }
}