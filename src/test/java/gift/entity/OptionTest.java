package gift.entity;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import gift.exception.InvalidQuantityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class OptionTest {

    private Option option;

    @BeforeEach
    void setUp() {
        Product product = new Product("Test Product", 1000, "test.com");
        option = new Option(product, "Test Option", 100);
    }

    @Test
    void subtractValidQuantity() {
        option.subtractOptionNum(60);
        assertThat(option.getQuantity()).isEqualTo(40);
    }

    @Test
    void subtractNegativeQuantity() {
        assertThatThrownBy(() -> option.subtractOptionNum(-1))
            .isInstanceOf(InvalidQuantityException.class)
            .hasMessage("감소 수량은 0 이상이어야 합니다.");
    }

    @Test
    void subtractExceedsQuantity() {
        assertThatThrownBy(() -> option.subtractOptionNum(101))
            .isInstanceOf(InvalidQuantityException.class)
            .hasMessage("옵션 수량이 부족합니다.");
    }

}
