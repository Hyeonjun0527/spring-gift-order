package gift.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gift.dto.PaginationResponse;
import gift.dto.ProductRequest;
import gift.dto.ProductResponse;
import gift.entity.Product;
import gift.repository.OptionRepository;
import gift.repository.ProductRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OptionRepository optionRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createProductNormalProductResponse() {
        ProductRequest request = new ProductRequest(
            "Product(1+1)",
            1000,
            "test.com"
        );
        Product savedProduct = new Product(
            1L,
            "Product(1+1)",
            1000,
            "test.com"
        );
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        ProductResponse response = productService.createProduct(request);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Product(1+1)");
        assertThat(response.price()).isEqualTo(1000);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void createProductLen16ProductException() {
        ProductRequest request = new ProductRequest(
            "1234567890abcdef",
            1000,
            "test.com"
        );
        assertThatThrownBy(() -> productService.createProduct(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("상품 이름은 최대 15자까지 입력 가능합니다.");
        verify(productRepository, never()).save(any());
    }

    @Test
    void createProductInvalidProductException() {
        ProductRequest request = new ProductRequest(
            "달팽이@ 크림",
            1000,
            "test.com"
        );
        assertThatThrownBy(() -> productService.createProduct(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("허용되지 않은 특수 문자가 포함되었습니다.");
        verify(productRepository, never()).save(any());
    }

    @Test
    void createProductKakaodProductException() {
        ProductRequest request = new ProductRequest(
            "카카오 우산",
            1000,
            "test.com"
        );
        assertThatThrownBy(() -> productService.createProduct(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("상품명에 '카카오'가 포함되었습니다. 담당자와 협의가 필요합니다.");
        verify(productRepository, never()).save(any());
    }

    @Test
    void getProductNormalProductResponse() {
        Product product = new Product(
            1L,
            "Product(1+1)",
            1000,
            "test.com"
        );
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductResponse response = productService.getProduct(1L);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Product(1+1)");
        verify(productRepository).findById(1L);
    }

    @Test
    void getProductInvalidProductException() {
        when(productRepository.findById(123L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProduct(123L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Product(id: 123) not found");
        verify(productRepository).findById(123L);
    }

    @Test
    void updateProductNormalProductResponse() {
        Product existingProduct = new Product(
            1L,
            "Product(1+1)",
            1000,
            "test.com"
        );
        Product updatedProduct = new Product(
            1L,
            "Product(2+1)",
            1500,
            "test.com"
        );
        ProductRequest request = new ProductRequest(
            "Product(2+1)",
            1500,
            "test.com"
        );
        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        ProductResponse response = productService.updateProduct(1L, request);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Product(2+1)");
        assertThat(response.price()).isEqualTo(1500);
        verify(productRepository).findById(1L);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void updateProductLen16ProductException() {
        ProductRequest request = new ProductRequest(
            "1234567890abcdef",
            1000,
            "test.com"
        );
        assertThatThrownBy(() -> productService.updateProduct(1L, request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("상품 이름은 최대 15자까지 입력 가능합니다.");
        verify(productRepository, never()).save(any());
    }

    @Test
    void updateProductInvalidProductException() {
        ProductRequest request = new ProductRequest(
            "달팽이@ 크림",
            1000,
            "test.com"
        );
        assertThatThrownBy(() -> productService.updateProduct(1L, request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("허용되지 않은 특수 문자가 포함되었습니다.");
        verify(productRepository, never()).save(any());
    }

    @Test
    void updateProductKakaodProductException() {
        ProductRequest request = new ProductRequest(
            "카카오 우산",
            1000,
            "test.com"
        );
        assertThatThrownBy(() -> productService.updateProduct(1L, request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("상품명에 '카카오'가 포함되었습니다. 담당자와 협의가 필요합니다.");
        verify(productRepository, never()).save(any());
    }

    @Test
    void deleteProductNormalProduct() {
        when(productRepository.findById(1L)).thenReturn(
            Optional.of(new Product(1L, "test product", 1000, "test.com")));

        productService.deleteProduct(1L);

        verify(productRepository).findById(1L);
        verify(productRepository).deleteById(1L);
    }

    @Test
    void deleteProductInvalidProductException() {
        when(productRepository.findById(123L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.deleteProduct(123L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Product(id: 123) not found");
        verify(productRepository).findById(123L);
        verify(productRepository, never()).delete(any());
    }

    @Test
    void getAllProductsTest() {
        List<Product> products = List.of(
            new Product(1L, "test product1", 1000, "test.com"),
            new Product(2L, "test product2", 1500, "test.com")
        );
        when(productRepository.findAll()).thenReturn(products);

        List<ProductResponse> responses = productService.getAllProducts();

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).name()).isEqualTo("test product1");
        assertThat(responses.get(1).name()).isEqualTo("test product2");
        verify(productRepository).findAll();
    }

    @Test
    void getAllProductsPagedNormalCase() {
        List<Product> products = List.of(
            new Product(1L, "Product 1", 1000, "test.com"),
            new Product(2L, "Product 2", 1500, "test.com"),
            new Product(3L, "Product 3", 2000, "test.com"),
            new Product(4L, "Product 4", 2500, "test.com"),
            new Product(5L, "Product 5", 3000, "test.com")
        );
        Pageable pageable = PageRequest.of(0, 5);
        Page<Product> page = new PageImpl<>(products, pageable, 15L);
        when(productRepository.findAll(pageable)).thenReturn(page);

        PaginationResponse<ProductResponse> response = productService.getAllProductsPaged(pageable);

        assertThat(response.content()).hasSize(5);
        assertThat(response.totalPages()).isEqualTo(3);
        assertThat(response.currentPages()).isEqualTo(0);
        assertThat(response.content().getFirst().name()).isEqualTo("Product 1");
        verify(productRepository).findAll(pageable);
    }

}
