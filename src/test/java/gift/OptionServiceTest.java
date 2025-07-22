package gift;

import gift.entity.Option;
import gift.entity.Product;
import gift.repository.OptionRepository;
import gift.repository.ProductRepository;
import gift.service.OptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@Import(OptionService.class)
class OptionServiceTest {

    @Autowired
    private OptionService optionService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OptionRepository optionRepository;

    private Option savedOption;

    @BeforeEach
    void setUp() {
        Product product = productRepository.save(new Product("테스트 상품", 10000, "img.jpg"));
        Option option = new Option("테스트 옵션", 100, product);
        savedOption = optionRepository.save(option);
    }

    @Test
    void 옵션_수량을_차감() {
        // when
        optionService.reduceOptionQuantity(savedOption.getId(), 1);
        Option updatedOption = optionRepository.findById(savedOption.getId()).orElseThrow();

        // then
        assertThat(updatedOption.getQuantity()).isEqualTo(99);
    }

    @Test
    void 수량보다_많이_차감하면_예외가_발생() {
        assertThrows(IllegalStateException.class, () -> {
            optionService.reduceOptionQuantity(savedOption.getId(), 101);
        });
    }
}
