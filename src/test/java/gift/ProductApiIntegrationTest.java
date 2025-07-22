package gift;

import gift.dto.ProductRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductApiIntegrationTest {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;

    String baseUrl;
    String adminToken;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/products";

        // 관리자 로그인 → 토큰 발급
        Map<String, String> adminLogin = Map.of("email", "admin@admin", "password", "admin123");
        ResponseEntity<Map> adminLoginRes = restTemplate.postForEntity("http://localhost:" + port + "/api/members/login", adminLogin, Map.class);
        adminToken = (String) adminLoginRes.getBody().get("token");

        // 테스트용 상품 등록
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> productReq = Map.of(
                "name", "테스트상품1",
                "price", 10000,
                "imageUrl", "http://test.com/image.jpg"
        );

        HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(productReq, headers);
        restTemplate.exchange(baseUrl, HttpMethod.POST, requestEntity, String.class);
    }


    @Test
    @DisplayName("상품 등록 성공 (관리자 권한)")
    void createProduct_success() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        ProductRequestDto productDto = new ProductRequestDto("테스트상품", 10000, "https://test.image");
        HttpEntity<ProductRequestDto> entity = new HttpEntity<>(productDto, headers);

        ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.POST, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).contains("테스트상품");
    }

    @Test
    @DisplayName("상품 목록 조회")
    void getProducts_success() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.GET, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("테스트상품1");
    }

    @Test
    @DisplayName("상품 단건 조회 성공")
    void getProductById_success() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/1", HttpMethod.GET, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("테스트상품1");
    }

    @Test
    @DisplayName("상품 수정 성공")
    void updateProduct_success() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        ProductRequestDto updateDto = new ProductRequestDto("수정상품", 20000, "https://updated.image");
        HttpEntity<ProductRequestDto> entity = new HttpEntity<>(updateDto, headers);

        ResponseEntity<String> response = restTemplate.exchange(baseUrl + "/1", HttpMethod.PUT, entity, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).contains("수정상품");
    }

    @Test
    @DisplayName("상품 삭제 성공")
    void deleteProduct_success() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Void> response = restTemplate.exchange(baseUrl + "/1", HttpMethod.DELETE, entity, Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
