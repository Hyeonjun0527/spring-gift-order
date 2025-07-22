package gift.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void showCreateFormProductFormView() throws Exception {
        mockMvc.perform(get("/admin/products/new"))
            .andExpect(status().isOk())
            .andExpect(view().name("admin/product-form"))
            .andExpect(model().attributeExists("productRequest"))
            .andExpect(model().attribute("action", "/admin/products/new"));
    }

    @Test
    void createProductNormalProductProductList() throws Exception {
        mockMvc.perform(post("/admin/products/new")
                .param("name", "Product(1+1)")
                .param("price", "1000")
                .param("imageUrl", "test.com"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/admin/products"))
            .andExpect(flash().attributeExists("message"));
    }

    @Test
    void createProductKakaoProductProductFormViewError() throws Exception {
        mockMvc.perform(post("/admin/products/new")
                .param("name", "카카오 우산")
                .param("price", "10000")
                .param("imageUrl", "test.com"))
            .andExpect(status().isOk())
            .andExpect(view().name("admin/product-form"))
            .andExpect(model().attributeExists("error"))
            .andExpect(model().attribute("error", "상품명에 '카카오'가 포함되었습니다. 담당자와 협의가 필요합니다."));
    }

    @Test
    void createProductLen16ProductProductFormViewError() throws Exception {
        mockMvc.perform(post("/admin/products/new")
                .param("name", "1234567890abcdef")
                .param("price", "1000")
                .param("imageUrl", "test.com"))
            .andExpect(status().isOk())
            .andExpect(view().name("admin/product-form"))
            .andExpect(model().attributeExists("error"))
            .andExpect(model().attribute("error", "상품 이름은 최대 15자까지 입력 가능합니다."));
    }

    @Test
    void createProductNameNullProductProductFormViewError() throws Exception {
        mockMvc.perform(post("/admin/products/new")
                .param("price", "1000")
                .param("imageUrl", "test.com"))
            .andExpect(status().isOk())
            .andExpect(view().name("admin/product-form"))
            .andExpect(model().attributeExists("error"))
            .andExpect(model().attribute("error", "상품명은 필수입니다."));
    }

    @Test
    void updateProductKakaoProductProductFormViewError() throws Exception {
        mockMvc.perform(post("/admin/products/1/edit")
                .param("name", "카카오 우산")
                .param("price", "20000")
                .param("imageUrl", "test.com"))
            .andExpect(status().isOk())
            .andExpect(view().name("admin/product-form"))
            .andExpect(model().attributeExists("error"))
            .andExpect(model().attribute("error", "상품명에 '카카오'가 포함되었습니다. 담당자와 협의가 필요합니다."));
    }

}