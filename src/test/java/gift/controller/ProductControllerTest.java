package gift.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import gift.dto.OptionResponse;
import gift.entity.Option;
import gift.entity.Product;
import gift.service.ProductService;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

public class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getProductOptionsSuccess() {
        String optionName = "01_[Best] Hand Cream";
        Product product = new Product("Test Product", 1000, "test.com");
        product.addOption(optionName, 100);
        Option option = product.getOptions().get(0);
        when(productService.getProductToEntity(1L)).thenReturn(product);

        ResponseEntity<List<OptionResponse>> response = productController.getProductOptions(1L);

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        List<OptionResponse> options = response.getBody();
        assertThat(options).hasSize(2);
        OptionResponse optionResponse = options.get(1);
        assertThat(optionResponse.id()).isEqualTo(option.getId());
        assertThat(optionResponse.name()).isEqualTo(optionName);
        assertThat(optionResponse.quantity()).isEqualTo(100);
    }

}
