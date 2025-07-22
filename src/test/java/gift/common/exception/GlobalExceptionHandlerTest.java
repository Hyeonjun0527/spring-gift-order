package gift.common.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.product.adapter.web.ProductController;
import gift.product.application.port.in.ProductUseCase;
import gift.product.domain.model.Option;
import gift.product.domain.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(ProductController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductUseCase productUseCase;

    @Test
    @DisplayName("NullPointerException - 필수 값 누락")
    void handleNullPointerException() throws Exception {
        // given
        Product invalidRequest = Product.create(1L,"상품", 1, null, List.of(Option.of(1L,1L,"옵션",1)));

        // when
        MockHttpServletResponse response = mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andDo(print())
                .andReturn()
                .getResponse();

        // then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(response.getContentAsString()).contains("REQUIRED_VALUE_MISSING");
        assertThat(response.getContentAsString()).contains("필수 입력값이 누락되었습니다");
        assertThat(response.getContentAsString()).contains("/api/products");
    }
} 