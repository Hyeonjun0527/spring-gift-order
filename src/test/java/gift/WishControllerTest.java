package gift;

import gift.dto.WishRequestDto;
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
class WishControllerTest {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;

    String baseUrl;
    String userToken;
    String adminToken;
    Long productId;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;

        // 1. 관리자 로그인
        Map<String, String> adminLogin = Map.of("email", "admin@admin", "password", "admin123");
        ResponseEntity<Map> adminLoginRes = restTemplate.postForEntity(baseUrl + "/api/members/login", adminLogin, Map.class);
        assertThat(adminLoginRes.getStatusCode()).isEqualTo(HttpStatus.OK);
        adminToken = (String) adminLoginRes.getBody().get("token");

        // 2. 테스트용 상품 추가
        HttpHeaders adminHeaders = new HttpHeaders();
        adminHeaders.setBearerAuth(adminToken);
        adminHeaders.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> productReq = Map.of("name", "테스트상품1", "price", 10000, "imageUrl", "test.jpg");
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
    @DisplayName("위시리스트 추가 및 조회 테스트")
    void addAndGetWishlist() {
        // 1. 위시리스트 추가
        WishRequestDto wishReq = new WishRequestDto(productId, 2);
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(userToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<WishRequestDto> addEntity = new HttpEntity<>(wishReq, headers);
        ResponseEntity<String> addRes = restTemplate.exchange(baseUrl + "/wishlist", HttpMethod.POST, addEntity, String.class);

        assertThat(addRes.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(addRes.getBody()).contains("\"product\":{\"id\":" + productId);

        // 2. 위시리스트 조회
        HttpEntity<Void> getEntity = new HttpEntity<>(headers);
        ResponseEntity<String> getRes = restTemplate.exchange(baseUrl + "/wishlist", HttpMethod.GET, getEntity, String.class);

        assertThat(getRes.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getRes.getBody()).contains("\"product\":{\"id\":" + productId);
    }

    @Test
    @DisplayName("위시리스트 중복 추가 시 예외 발생")
    void duplicateWishlist() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(userToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        WishRequestDto wishReq = new WishRequestDto(productId, 1);
        HttpEntity<WishRequestDto> entity = new HttpEntity<>(wishReq, headers);

        // 첫 번째 추가
        restTemplate.exchange(baseUrl + "/wishlist", HttpMethod.POST, entity, String.class);

        // 두 번째 추가 (중복)
        ResponseEntity<String> dupRes = restTemplate.exchange(baseUrl + "/wishlist", HttpMethod.POST, entity, String.class);

        assertThat(dupRes.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(dupRes.getBody()).contains("이미 위시리스트에 추가된 상품입니다.");
    }
}
