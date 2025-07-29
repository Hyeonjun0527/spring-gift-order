package gift.product.adapter.persistence.entity;

import gift.product.adapter.persistence.repository.ProductJpaRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@TestPropertySource(properties = "spring.jpa.show-sql=true")
public class ProductOptionUpdateTest {

    private static final Logger log = LoggerFactory.getLogger(ProductOptionUpdateTest.class);

    @Autowired
    private ProductJpaRepository productJpaRepository;

    @Autowired
    private EntityManager entityManager;

    private Long productId;

    @BeforeEach
    void setUp() {
        // given: 초기 데이터 설정 (옵션 A, B를 가진 상품)
        ProductEntity product = ProductEntity.create("테스트 상품", 1000, "test.jpg", List.of());
        product.addOption(new OptionEntity("옵션A", 10));
        product.addOption(new OptionEntity("옵션B", 20));

        ProductEntity savedProduct = productJpaRepository.save(product);
        this.productId = savedProduct.getId();
        entityManager.flush();
        entityManager.clear(); // 영속성 컨텍스트 초기화
    }

    @Test
    @DisplayName("setOptionsByClearAndAdd: 옵션 하나만 수정해도 DELETE와 INSERT 쿼리가 모두 발생한다")
    void updateOptions_ByClearAndAdd() {
        log.info("\n--- [Test Start] 비효율적인 방식(Clear & Add) ---");

        // when: 옵션 A의 수량을 변경하고, 옵션 C를 새로 추가 (옵션 B는 제거)
        ProductEntity productToUpdate = productJpaRepository.findById(productId).orElseThrow();
        OptionEntity updatedOptionA = new OptionEntity("옵션A", 15);
        OptionEntity newOptionC = new OptionEntity("옵션C", 30);

        productToUpdate.setOptionsByClearAndAdd(List.of(updatedOptionA, newOptionC));
        entityManager.flush(); // 변경 감지 및 DB 반영 강제 실행

        log.info("--- [Test End] 비효율적인 방식(Clear & Add) ---\n");

        // then: 최종 상태 검증
        ProductEntity result = productJpaRepository.findById(productId).orElseThrow();
        assertThat(result.getOptions()).hasSize(2);
        assertThat(result.getOptions()).extracting("name").containsExactlyInAnyOrder("옵션A", "옵션C");
        assertThat(result.getOptions()).extracting("quantity").containsExactlyInAnyOrder(15, 30);
    }

    @Test
    @DisplayName("setOptions(효율적 방식): 변경된 내용에 대해서만 UPDATE, INSERT, DELETE 쿼리가 발생한다")
    void updateOptions_Efficiently() {
        log.info("\n--- [Test Start] 효율적인 방식 (Smart Update) ---");

        // when: 옵션 A의 수량을 변경하고, 옵션 C를 새로 추가 (옵션 B는 제거)
        ProductEntity productToUpdate = productJpaRepository.findById(productId).orElseThrow();
        
        // 1. 기존 옵션(B)를 컬렉션에서 제거합니다. (orphanRemoval=true에 의해 DELETE 쿼리 발생)
        productToUpdate.getOptions().removeIf(o -> o.getName().equals("옵션B"));

        // 2. 기존 옵션(A)의 내용을 직접 변경합니다. (UPDATE 쿼리 발생)
        productToUpdate.getOptions().stream()
                .filter(o -> o.getName().equals("옵션A"))
                .findFirst()
                .ifPresent(optionA -> optionA.updateOption("옵션A", 15)); // 수량만 변경

        // 3. 새로운 옵션(C)을 생성하여 컬렉션에 추가합니다. (INSERT 쿼리 발생)
        productToUpdate.addOption(new OptionEntity("옵션C", 30));

        entityManager.flush(); // 변경 감지 및 DB 반영 강제 실행

        log.info("--- [Test End] 효율적인 방식 (Smart Update) ---\n");

        // then: 최종 상태 검증
        ProductEntity result = productJpaRepository.findById(productId).orElseThrow();
        assertThat(result.getOptions()).hasSize(2);
        assertThat(result.getOptions()).extracting("name").containsExactlyInAnyOrder("옵션A", "옵션C");
        assertThat(result.getOptions()).extracting("quantity").containsExactlyInAnyOrder(15, 30);
    }
} 