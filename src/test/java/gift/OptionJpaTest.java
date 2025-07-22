package gift;

import gift.dto.OptionRequestDto;
import gift.dto.OptionResponseDto;
import gift.entity.Option;
import gift.entity.Product;
import gift.repository.OptionRepository;
import gift.repository.ProductRepository;
import gift.service.OptionService;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@DataJpaTest
class OptionJpaTest {

    @Autowired
    OptionRepository optionRepository;

    @Autowired
    private EntityManager entityManager;

    Product product;

    @BeforeEach
    void setUp() {
        product = new Product("테스트 상품", 3000, "test.jpg");
        entityManager.persist(product);
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    void 옵션을_생성_및_조회() {
        // given
        Option option = new Option("테스트 옵션", 10, product);
        // when
        optionRepository.save(option);
        Option found = optionRepository.findById(option.getId()).orElseThrow();

        // then
        assertThat(found.getName()).isEqualTo("테스트 옵션");
        assertThat(found.getQuantity()).isEqualTo(10);
    }

    @Test
    void 옵션을_수정() {
        // given
        Option option = new Option("테스트 옵션", 10, product);
        entityManager.persist(option);
        entityManager.flush();
        entityManager.clear();

        // when
        Option found = optionRepository.findById(option.getId()).orElseThrow();
        Option newOption = found.updateTo("수정된 옵션", 1);
        optionRepository.save(newOption);
        entityManager.flush();
        entityManager.clear();

        // then
        Option updated = optionRepository.findById(newOption.getId()).orElseThrow();
        assertThat(updated.getName()).isEqualTo("수정된 옵션");
        assertThat(updated.getQuantity()).isEqualTo(1);
    }

    @Test
    void 옵션_삭제() {
        // given
        Option option = new Option("테스트 옵션", 10, product);
        entityManager.persist(option);
        entityManager.flush();
        entityManager.clear();

        // when
        optionRepository.delete(option);
        entityManager.flush();
        entityManager.clear();

        // then
        boolean exists = optionRepository.findById(option.getId()).isPresent();
        assertThat(exists).isFalse();
    }
}
