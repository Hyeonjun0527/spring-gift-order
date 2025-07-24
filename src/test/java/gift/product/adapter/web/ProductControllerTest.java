package gift.product.adapter.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.product.application.port.in.ProductUseCase;
import gift.product.application.port.in.dto.CreateProductRequest;
import gift.product.application.port.in.dto.OptionRequest;
import gift.product.application.port.in.dto.ProductResponse;
import gift.product.application.port.in.dto.UpdateProductRequest;
import gift.product.domain.model.Option;
import gift.product.domain.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductUseCase productUseCase;

    private List<Option> createMockOp(Long id,Long productId) {
        return List.of(Option.of(id,productId, "옵션", 1));
    }

    @Test
    @DisplayName("페이지 상품 조회")
    void getAllProducts() throws Exception {
        // given
        Long productId = 1L;
        Long productId2 = 2L;
        Product product1 = Product.create(productId, "상품1", 1000, "image1.jpg",createMockOp(1L,productId));
        Product product2 = Product.create(productId2, "상품2", 2000, "image2.jpg",createMockOp(2L,productId2));
        
        Page<Product> productPage = new PageImpl<>(
            List.of(product1, product2), 
            PageRequest.of(0, 20),
            1000
        );
        given(productUseCase.getProducts(any(Pageable.class))).willReturn(productPage);

        // when
        MockHttpServletResponse response = mockMvc.perform(get("/api/products")
                        .param("page", "0")
                        .param("size", "20"))
                .andDo(print())
                .andReturn()
                .getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(response.getContentType()).isEqualTo(MediaType.APPLICATION_JSON_VALUE);
        
        // 응답 내용에 상품 데이터가 포함되어 있는지 확인
        String responseContent = response.getContentAsString();
        assertThat(responseContent).contains("상품1");
        assertThat(responseContent).contains("상품2");
        assertThat(responseContent).contains("1000");
    }

    @Test
    @DisplayName("ID로 상품 조회")
    void getProductById() throws Exception {
        // given
        Long productId = 1L;
        Product product = Product.create(productId, "Test Product", 100, "test.jpg",createMockOp(1L,productId));
        given(productUseCase.getProduct(productId)).willReturn(product);

        // when
        MockHttpServletResponse response = mockMvc.perform(get("/api/products/{id}", productId))
                .andDo(print())
                .andReturn()
                .getResponse();

        // then
        ProductResponse result = objectMapper.readValue(response.getContentAsString(), ProductResponse.class);
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(result.id()).isEqualTo(productId);
    }

    @Test
    @DisplayName("상품 추가")
    void addProduct() throws Exception {
        // given
        Long productId = 1L;
        Product request = Product.create(productId,"New Product", 100,"new.jpg", createMockOp(1L,productId) );
        given(productUseCase.addProduct(any(CreateProductRequest.class))).willReturn(request);

        // when
        MockHttpServletResponse response = mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andReturn()
                .getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(response.getHeader("Location")).isEqualTo("/api/products/1");
    }

    @Test
    @DisplayName("상품 수정")
    void updateProduct() throws Exception {
        // given
        Long productId = 1L;
        List<OptionRequest> optionRequests = List.of(new OptionRequest("Updated Option", 150));
        UpdateProductRequest request = new UpdateProductRequest("Updated Product", 150, "updated.jpg", optionRequests);

        // when
        MockHttpServletResponse response = mockMvc.perform(put("/api/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andReturn()
                .getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
        verify(productUseCase).updateProduct(productId, request);
    }

    @Test
    @DisplayName("상품 삭제")
    void deleteProduct() throws Exception {
        // given
        Long productId = 1L;

        // when
        MockHttpServletResponse response = mockMvc.perform(delete("/api/products/{id}", productId))
                .andDo(print())
                .andReturn()
                .getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NO_CONTENT.value());
        verify(productUseCase).deleteProduct(productId);
    }
}