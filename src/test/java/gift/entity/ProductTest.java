package gift.entity;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import gift.exception.InvalidOptionNameException;
import gift.exception.InvalidQuantityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProductTest {

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product("Test Product", 1000, "test.com");
    }

    @Test
    void addOptionValidCase() {
        product.addOption("Best1", 100);
        Option option = product.getOptions().get(1);
        assertThat(option.getName()).isEqualTo("Best1");
        assertThat(option.getQuantity()).isEqualTo(100);
    }

    @Test
    void addOptionDuplicateName() {
        String optionName = "Option1";
        product.addOption(optionName, 100);
        assertThatThrownBy(() -> product.addOption(optionName, 10))
            .isInstanceOf(InvalidOptionNameException.class)
            .hasMessage("동일한 옵션 이름이 이미 존재합니다.");
    }

    @Test
    void addOptionInvalidLength() {
        String longName = "a".repeat(51);
        assertThatThrownBy(() -> product.addOption(longName, 100))
            .isInstanceOf(InvalidOptionNameException.class)
            .hasMessage("옵션 이름은 최대 50자까지 가능합니다.");
    }

    @Test
    void addOptionInvalidName() {
        assertThatThrownBy(() -> product.addOption("Test@", 100))
            .isInstanceOf(InvalidOptionNameException.class)
            .hasMessage("허용되지 않은 특수 문자가 포함되었습니다.");
    }

    @Test
    void addOptionInvalidQuantity() {
        assertThatThrownBy(() -> product.addOption("Option1", 0))
            .isInstanceOf(InvalidQuantityException.class)
            .hasMessage("옵션 수량은 1이상 1억 미만이어야 합니다.");
        assertThatThrownBy(() -> product.addOption("Option2", 100000000))
            .isInstanceOf(InvalidQuantityException.class)
            .hasMessage("옵션 수량은 1이상 1억 미만이어야 합니다.");
    }

}
