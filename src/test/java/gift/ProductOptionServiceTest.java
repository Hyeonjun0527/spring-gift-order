package gift;

import gift.dto.ProductOptionRequestDto;
import gift.entity.Product;
import gift.entity.ProductOption;
import gift.repository.ProductOptionRepository;
import gift.repository.ProductRepository;
import gift.service.ProductOptionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductOptionServiceTest {

    @Mock
    private ProductOptionRepository optionRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductOptionService optionService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product("테스트상품", 1000, "image.jpg");
        product.setId(1L);
    }


    @Test
    @DisplayName("옵션 이름이 50자 초과면 예외 발생")
    void validateOptionNameTooLong() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        String longName = "a".repeat(51);
        ProductOptionRequestDto dto = new ProductOptionRequestDto(longName, 10);

        assertThatThrownBy(() -> optionService.addOption(1L, dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("옵션 이름은 50자 이하");
    }

    @Test
    @DisplayName("옵션 이름에 허용되지 않은 특수문자가 포함되면 예외 발생")
    void validateOptionNameInvalidChars() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        String invalidName = "옵션이름$%^";
        ProductOptionRequestDto dto = new ProductOptionRequestDto(invalidName, 10);

        assertThatThrownBy(() -> optionService.addOption(1L, dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("허용되지 않는 문자가");
    }

    @Test
    @DisplayName("옵션 수량이 1 미만이면 예외 발생")
    void validateOptionQuantityTooSmall() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        ProductOptionRequestDto dto = new ProductOptionRequestDto("옵션", 0);

        assertThatThrownBy(() -> optionService.addOption(1L, dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("수량은 1 이상");
    }

    @Test
    @DisplayName("동일 상품 내 옵션 이름 중복 시 예외 발생")
    void duplicateOptionName() {
        ProductOptionRequestDto dto = new ProductOptionRequestDto("옵션1", 10);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(optionRepository.existsByProductIdAndName(1L, "옵션1")).thenReturn(true);

        assertThatThrownBy(() -> optionService.addOption(1L, dto))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("같은 옵션 이름이 존재");
    }

    @Test
    @DisplayName("옵션 수량 차감 성공")
    void subtractOptionQuantity() {
        ProductOption option = new ProductOption(product, "옵션", 100);
        option.setId(1L);
        when(optionRepository.findById(1L)).thenReturn(Optional.of(option));

        optionService.subtractOptionQuantity(1L, 10);

        assertThat(option.getQuantity()).isEqualTo(90);
    }

    @Test
    @DisplayName("옵션 수량 차감 시 재고 부족 예외")
    void subtractOptionInsufficientStock() {
        ProductOption option = new ProductOption(product, "옵션", 5);
        option.setId(1L);
        when(optionRepository.findById(1L)).thenReturn(Optional.of(option));

        assertThatThrownBy(() -> optionService.subtractOptionQuantity(1L, 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("수량이 부족합니다.");
    }

}
