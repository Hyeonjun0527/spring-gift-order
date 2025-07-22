package gift;

import gift.entity.Product;
import gift.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        // 초기 데이터 저장
        productRepository.save(new Product("테스트상품1", 10000, "http://test.com/1.jpg"));
        productRepository.save(new Product("테스트상품2", 20000, "http://test.com/2.jpg"));
    }

    @Test
    @DisplayName("상품 저장 테스트")
    void saveProduct() {
        Product product = new Product("테스트상품3", 30000, "http://test.com/3.jpg");
        Product savedProduct = productRepository.save(product);

        assertThat(savedProduct.getId()).isNotNull();
        assertThat(savedProduct.getName()).isEqualTo("테스트상품3");
    }

    @Test
    @DisplayName("전체 상품 조회 테스트")
    void findAllProducts() {
        List<Product> products = productRepository.findAll();
        assertThat(products.size()).isEqualTo(2);
        assertThat(products).extracting(Product::getName).contains("테스트상품1", "테스트상품2");
    }

    @Test
    @DisplayName("상품 ID로 조회 테스트")
    void findProductById() {
        Product product = productRepository.findAll().get(0);
        Optional<Product> foundProduct = productRepository.findById(product.getId());

        assertThat(foundProduct).isPresent();
        assertThat(foundProduct.get().getName()).isEqualTo(product.getName());
    }

    @Test
    @DisplayName("상품 삭제 테스트")
    void deleteProduct() {
        Product product = productRepository.findAll().get(0);
        productRepository.deleteById(product.getId());

        Optional<Product> deleted = productRepository.findById(product.getId());
        assertThat(deleted).isEmpty();
    }
}
