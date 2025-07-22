package gift;

import gift.dto.ProductOptionRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ProductOptionControllerTest {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;

    String baseUrl;
    String adminToken;
    String userToken;
    Long productId;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;

        // 1. 관리자 로그인
        Map<String, String> adminLogin = Map.of("email", "admin@admin", "password", "admin123");
        ResponseEntity<Map> adminLoginRes = restTemplate.postForEntity(baseUrl + "/api/members/login", adminLogin, Map.class);
        assertThat(adminLoginRes.getStatusCode()).isEqualTo(HttpStatus.OK);
        adminToken = (String) adminLoginRes.getBody().get("token");

        // 2. 상품 추가 (관리자 권한)
        HttpHeaders adminHeaders = new HttpHeaders();
        adminHeaders.setBearerAuth(adminToken);
        adminHeaders.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> productReq = Map.of("name", "테스트상품", "price", 5000, "imageUrl", "test.jpg");
        HttpEntity<Map<String, Object>> productEntity = new HttpEntity<>(productReq, adminHeaders);
        ResponseEntity<Map> productRes = restTemplate.exchange(baseUrl + "/api/products", HttpMethod.POST, productEntity, Map.class);
        assertThat(productRes.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        productId = Long.valueOf(productRes.getBody().get("id").toString());

        // 3. 일반 사용자 회원가입 및 로그인
        String email = "user" + UUID.randomUUID().toString().substring(0, 6) + "@test.com";
        Map<String, String> registerReq = Map.of("email", email, "password", "123456");
        restTemplate.postForEntity(baseUrl + "/api/members/register", registerReq, String.class);
        Map<String, String> loginReq = Map.of("email", email, "password", "123456");
        ResponseEntity<Map> loginRes = restTemplate.postForEntity(baseUrl + "/api/members/login", loginReq, Map.class);
        assertThat(loginRes.getStatusCode()).isEqualTo(HttpStatus.OK);
        userToken = (String) loginRes.getBody().get("token");
    }

    @Test
    @DisplayName("옵션 목록 조회 - 옵션 존재")
    void getOptions_success() {
        // 4. 옵션 추가 (관리자 권한)
        HttpHeaders adminHeaders = new HttpHeaders();
        adminHeaders.setBearerAuth(adminToken);
        adminHeaders.setContentType(MediaType.APPLICATION_JSON);
        ProductOptionRequestDto optionDto = new ProductOptionRequestDto("옵션1", 10);
        HttpEntity<ProductOptionRequestDto> optionEntity = new HttpEntity<>(optionDto, adminHeaders);
        ResponseEntity<String> optionRes = restTemplate.exchange(
                baseUrl + "/api/products/" + productId + "/options",
                HttpMethod.POST,
                optionEntity,
                String.class
        );
        assertThat(optionRes.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // 5. 사용자 토큰으로 옵션 목록 조회
        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.setBearerAuth(userToken);
        HttpEntity<Void> userEntity = new HttpEntity<>(userHeaders);

        ResponseEntity<String> getRes = restTemplate.exchange(
                baseUrl + "/api/products/" + productId + "/options?page=0&size=5",
                HttpMethod.GET,
                userEntity,
                String.class
        );

        assertThat(getRes.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getRes.getBody()).contains("옵션1");
    }

    @Test
    @DisplayName("옵션 목록 조회 - 빈 목록")
    void getOptions_empty() {
        // ✅ 사용자 토큰으로 존재하지 않는 상품 옵션 조회
        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.setBearerAuth(userToken);
        HttpEntity<Void> userEntity = new HttpEntity<>(userHeaders);

        ResponseEntity<String> getRes = restTemplate.exchange(
                baseUrl + "/api/products/99999/options?page=0&size=5",
                HttpMethod.GET,
                userEntity,
                String.class
        );

        assertThat(getRes.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getRes.getBody()).contains("\"content\":[]");
    }
}
