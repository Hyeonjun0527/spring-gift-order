package gift.product.adapter.persistence.adapter;

import gift.product.adapter.persistence.entity.OptionEntity;
import gift.product.adapter.persistence.entity.ProductEntity;
import gift.product.adapter.persistence.repository.ProductJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class EntityStateTest {

    private static final Logger log = LoggerFactory.getLogger(EntityStateTest.class);

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private ProductJpaRepository productJpaRepository;

    private Long savedProductId;

    @BeforeEach
    void setUp() {
        // 테스트를 위해 미리 데이터를 하나 저장해둡니다.
        ProductEntity originalProduct = ProductEntity.create("원본 상품", 10000, "original.jpg", new ArrayList<>());
        ProductEntity savedEntity = testEntityManager.persistFlushFind(originalProduct);
        this.savedProductId = savedEntity.getId();
        log.info("--- 테스트 데이터 준비 완료 (ID: {}) ---", savedProductId);

        // 영속성 컨텍스트를 초기화하여 'savedEntity'를 준영속 상태로 만듭니다.
        testEntityManager.clear();
    }

    @Test
    @DisplayName("ID를 가진 비영속 객체를 save하면, JPA는 이를 준영속처럼 취급하여 merge를 수행한다")
    void test_transient_with_id_is_merged() {
        log.info("--- 1. ID({})를 가진 '비영속' 객체 생성 ---", savedProductId);
        // 정의: 이 객체는 영속 컨텍스트에서 관리된 적 없으므로 '비영속(Transient)' 상태입니다.
        // 하지만 DB에 존재하는 ID를 가지고 있습니다.
        ProductEntity transientWithId = ProductEntity.of(savedProductId, "새로운 이름", 9999, "new.jpg", new ArrayList<>());

        log.info("--- 2. productJpaRepository.save() 호출 (내부적으로 merge 동작 예상) ---");
        // save()는 ID 존재 여부를 보고 persist() 또는 merge()를 결정합니다.
        // ID가 있으므로 merge()가 호출될 것입니다.
        productJpaRepository.save(transientWithId);

        // flush를 호출하여 DB에 변경사항을 강제 반영합니다.
        testEntityManager.flush();
        log.info("--- DB 반영 완료 ---");

        log.info("--- 3. 검증 ---");
        ProductEntity foundEntity = productJpaRepository.findById(savedProductId).orElseThrow();
        assertThat(foundEntity.getName()).isEqualTo("새로운 이름");
        assertThat(foundEntity.getPrice()).isEqualTo(9999);
        log.info("--- 검증 완료: 상품 이름이 '{}'(으)로 변경됨 ---", foundEntity.getName());
    }

    @Test
    @DisplayName("비교: 진짜 '준영속' 객체를 merge하는 경우")
    void test_true_detached_entity_behavior() {
        log.info("--- 1. DB에서 엔티티 조회 후 '준영속' 상태로 만듦 ---");
        ProductEntity originalEntity = productJpaRepository.findById(savedProductId).orElseThrow();
        // detach를 통해 명시적으로 준영속 상태로 변경합니다.
        testEntityManager.detach(originalEntity);

        log.info("--- 2. 준영속 객체의 내용 변경 ---");
        originalEntity.changeName("진짜 준영속 객체 이름 변경");

        log.info("--- 3. entityManager.merge() 호출 ---");
        productJpaRepository.save(originalEntity); // 이것 또한 내부적으로 merge를 호출
        testEntityManager.flush();
        log.info("--- DB 반영 완료 ---");

        log.info("--- 4. 검증 ---");
        ProductEntity foundEntity = productJpaRepository.findById(savedProductId).orElseThrow();
        assertThat(foundEntity.getName()).isEqualTo("진짜 준영속 객체 이름 변경");
        log.info("--- 검증 완료: 상품 이름이 '{}'(으)로 변경됨 ---", foundEntity.getName());
    }

    @Test
    @DisplayName("위험: 준영속 자식 컬렉션을 가진 엔티티를 merge하면 예외가 발생할 수 있다")
    void test_merge_with_detached_children_throws_exception() {
        // --- 1. 사전 준비: 옵션을 가진 Product를 생성하고 모두 준영속 상태로 만들기 ---
        log.info("--- 1. 옵션을 포함한 테스트 데이터 생성 ---");
        testEntityManager.clear(); // 이전 테스트의 컨텍스트 정리

        ProductEntity initialProduct = ProductEntity.create("상품", 1000, "img.jpg", new ArrayList<>());
        initialProduct.addOption(new OptionEntity("옵션1", 10));
        initialProduct.addOption(new OptionEntity("옵션2", 20));

        ProductEntity persistedProduct = testEntityManager.persistFlushFind(initialProduct);
        Long productId = persistedProduct.getId();
        log.info("--- 테스트 데이터 준비 완료 (Product ID: {}) ---", productId);

        // 옵션을 포함한 모든 엔티티를 준영속 상태로 전환
        testEntityManager.clear();
        log.info("--- 모든 엔티티를 준영속 상태로 만듦 ---");

        // --- 2. 예외 발생 시나리오 ---
        log.info("--- 2. 새로운 영속성 컨텍스트에서 Product만 다시 조회 (영속 상태) ---");
        ProductEntity managedProduct = productJpaRepository.findById(productId).orElseThrow();

        log.info("--- 3. '준영속' 상태의 옵션 리스트를 생성 ---");
        List<OptionEntity> detachedOptions = new ArrayList<>();
        // ID가 없는 '비영속' 옵션. 하지만 CascadeType.ALL 때문에 persist 대상이 됨
        detachedOptions.add(new OptionEntity("새 옵션", 30));

        log.info("--- 4. '영속' 상태의 Product에 '준영속' 자식 컬렉션을 통째로 교체! (가장 위험한 부분) ---");
        // 이 메서드는 내부적으로 this.options = detachedOptions; 를 수행
        managedProduct.replaceOptionsForTest(detachedOptions);

        log.info("--- 5. save(merge) 실행 -> 예외 발생 예상 ---");
        // Hibernate는 영속상태인 managedProduct의 options 리스트가
        // 비영속/준영속 객체를 담은 새로운 리스트로 교체된 것을 보고 예외를 던진다.
        // CascadeType.ALL 때문에 새 옵션을 persist 하려 하지만,
        // 부모(product)의 options 컬렉션 자체가 영속성 컨텍스트가 관리하던 것이 아니기 때문.
        Assertions.assertThrows(org.springframework.orm.jpa.JpaSystemException.class, () -> {
            productJpaRepository.saveAndFlush(managedProduct);
        }, "A collection with cascade=\"all-delete-orphan\" was no longer referenced by the owning entity instance");
        log.info("--- 예상대로 'detached entity' 관련 예외 발생 성공! ---");
    }
} 