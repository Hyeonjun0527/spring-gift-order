package gift.repository;

import static org.assertj.core.api.Assertions.assertThat;

import gift.dto.ProductResponse;
import gift.entity.Member;
import gift.entity.Product;
import gift.entity.WishItem;
import java.util.List;
import java.util.Optional;
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
public class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private WishItemRepository wishItemRepository;

    private Product product;
    private Member member;
    private WishItem wishItem;

    @BeforeEach
    void setUp() {
        product = new Product("Test Product", 1000, "http://test.com");
        product = productRepository.save(product);

        member = new Member("test@test.com", "password123", "USER");
        member = memberRepository.save(member);

        wishItem = new WishItem(product, 2, member);
        product.getWishItems().add(wishItem);
        wishItem = wishItemRepository.save(wishItem);
    }

    @Test
    void save() {
        Product savedProduct = productRepository.save(product);

        assertThat(savedProduct.getId()).isNotNull();
        assertThat(savedProduct.getName()).isEqualTo("Test Product");
        assertThat(savedProduct.getPrice()).isEqualTo(1000);
        assertThat(savedProduct.getImageUrl()).isEqualTo("http://test.com");
    }

    @Test
    void findById() {
        productRepository.save(product);
        Optional<Product> foundProduct = productRepository.findById(product.getId());

        assertThat(foundProduct).isPresent();
        assertThat(foundProduct.get().getName()).isEqualTo("Test Product");
    }

    @Test
    void findByIdWithWishItems() {
        Product foundProduct = productRepository.findById(product.getId())
            .orElseThrow(() -> new RuntimeException("Product not found"));

        List<WishItem> wishItems = foundProduct.getWishItems();
        assertThat(wishItems).isNotEmpty();
        assertThat(wishItems).hasSize(1);
        assertThat(wishItems.getFirst().getProduct()).isEqualTo(foundProduct);
        assertThat(wishItems.getFirst().getMember()).isEqualTo(member);
        assertThat(wishItems.getFirst().getQuantity()).isEqualTo(2);
    }

    @Test
    void deleteById() {
        Product savedProduct = productRepository.save(product);
        productRepository.deleteById(savedProduct.getId());
        Optional<Product> deletedProduct = productRepository.findById(savedProduct.getId());

        assertThat(deletedProduct).isEmpty();
    }

    @Test
    void findAllPaged() {
        for (int i = 2; i <= 15; i++) {
            productRepository.save(new Product("Product" + i, 1000, "test.com"));
        }

        Pageable pageable = PageRequest.of(0, 5);
        Page<ProductResponse> page = productRepository.findAll(pageable)
            .map(product -> new ProductResponse(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getImageUrl()
            ));

        assertThat(page.getContent()).hasSize(5);
        assertThat(page.getTotalPages()).isEqualTo(3);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getContent().getFirst().name()).startsWith("Test Product");
    }

}