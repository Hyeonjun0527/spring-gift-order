package gift;

import gift.entity.Product;
import gift.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ProductJpaTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void 상품_저장_및_조회_성공() {
        // given
        Product product = new Product("아이스 아메리카노", 4500, "ice_americano.jpg");
        productRepository.save(product);

        // when
        Product found = productRepository.findById(product.getId()).orElseThrow();

        // then
        assertThat(found.getName()).isEqualTo("아이스 아메리카노");
        assertThat(found.getPrice()).isEqualTo(4500);
    }

    @Test
    void 상품_가격_수정_성공() {
        // given
        Product product = new Product("아이스 아메리카노", 4500, "ice_americano.jpg");
        entityManager.persist(product);
        entityManager.flush();
        entityManager.clear();

        // when
        Product found = productRepository.findById(product.getId()).orElseThrow();
        Product updatedProduct = found.updateTo("아이스 아메리카노", 4000, "ice_americano.jpg");
        productRepository.save(updatedProduct);

        entityManager.flush();
        entityManager.clear();

        Product updated = productRepository.findById(product.getId()).orElseThrow();

        // then
        assertThat(updated.getId()).isEqualTo(updatedProduct.getId());
        assertThat(updated.getPrice()).isEqualTo(4000);
    }

    @Test
    void 상품_페이지_조회_성공() {
        // given
        this.entityManager.persist(new Product("아이스 아메리카노", 4500, "ice_americano.jpg"));
        this.entityManager.persist(new Product("아이스 카페라떼", 5000, "ice_cafe_latte.jpg"));
        this.entityManager.persist(new Product("아인슈페너", 5500, "einspanner.jpg"));

        // when
        Pageable pageable = PageRequest.of(0, 2, Sort.by("id"));
        Page<Product> productsPage = productRepository.findAll(pageable);
        List<Product> found = productRepository.findAll(pageable).getContent();

        // then
        assertThat(found).hasSize(2);
        assertThat(productsPage.getSize()).isEqualTo(2);
    }
}