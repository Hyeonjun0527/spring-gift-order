package gift;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WishlistScenarioE2eTest {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;

    String baseUrl;
    String adminToken;
    String userToken;
    List<Long> productIds = new ArrayList<>();

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
    }

    @Test
    @DisplayName("전체 시나리오: 어드민/사용자 로그인, 상품 추가, 페이지네이션, 위시리스트 추가/조회/수정/삭제, 정렬 등")
    void fullScenario() {
        // 1. 어드민 로그인
        Map<String, String> adminLogin = Map.of("email", "admin@admin", "password", "admin123");
        ResponseEntity<Map> adminLoginRes = restTemplate.postForEntity(baseUrl + "/api/members/login", adminLogin, Map.class);
        assertThat(adminLoginRes.getStatusCode()).isEqualTo(HttpStatus.OK);
        adminToken = (String) adminLoginRes.getBody().get("token");

        // 2. 어드민 권한으로 상품 10개 추가
        HttpHeaders adminHeaders = new HttpHeaders();
        adminHeaders.setBearerAuth(adminToken);
        adminHeaders.setContentType(MediaType.APPLICATION_JSON);
        for (int i = 1; i <= 10; i++) {
            Map<String, Object> productReq = Map.of(
                    "name", "테스트상품" + i,
                    "price", i * 1000,
                    "imageUrl", "test" + i + ".jpg"
            );
            HttpEntity<Map<String, Object>> productEntity = new HttpEntity<>(productReq, adminHeaders);
            ResponseEntity<Map> productRes = restTemplate.postForEntity(baseUrl + "/api/products", productEntity, Map.class);
            assertThat(productRes.getStatusCode().is2xxSuccessful()).isTrue();
            Number id = (Number) ((Map)productRes.getBody()).get("id");
            productIds.add(id.longValue());
        }

        // 3. 일반 사용자 회원가입
        String userEmail = "user" + UUID.randomUUID().toString().substring(0, 8) + "@test.com";
        Map<String, String> registerReq = Map.of("email", userEmail, "password", "123456");
        ResponseEntity<Map> registerRes = restTemplate.postForEntity(baseUrl + "/api/members/register", registerReq, Map.class);
        assertThat(registerRes.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(registerRes.getBody()).containsKey("token");

        // 4. 일반 사용자 토큰으로 로그인
        Map<String, String> loginReq = Map.of("email", userEmail, "password", "123456");
        ResponseEntity<Map> loginRes = restTemplate.postForEntity(baseUrl + "/api/members/login", loginReq, Map.class);
        assertThat(loginRes.getStatusCode()).isEqualTo(HttpStatus.OK);
        userToken = (String) loginRes.getBody().get("token");

        // 5. 상품 페이지네이션 적용 조회 (5개씩 2페이지)
        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.setBearerAuth(userToken);
        HttpEntity<Void> userEntity = new HttpEntity<>(userHeaders);
        ResponseEntity<Map> page1Res = restTemplate.exchange(baseUrl + "/api/products?page=0&size=5", HttpMethod.GET, userEntity, Map.class);
        ResponseEntity<Map> page2Res = restTemplate.exchange(baseUrl + "/api/products?page=1&size=5", HttpMethod.GET, userEntity, Map.class);
        assertThat(page1Res.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(page2Res.getStatusCode()).isEqualTo(HttpStatus.OK);
        List<?> content1 = (List<?>) page1Res.getBody().get("content");
        List<?> content2 = (List<?>) page2Res.getBody().get("content");
        assertThat(content1).hasSize(5);
        assertThat(content2).hasSize(5);

        // 6. 위시리스트에 상품 추가 (1, 2, 3번 상품)
        for (int i = 0; i < 3; i++) {
            Map<String, Object> wishReq = Map.of(
                    "productId", productIds.get(i),
                    "quantity", i + 1
            );
            HttpEntity<Map<String, Object>> wishEntity = new HttpEntity<>(wishReq, userHeaders);
            ResponseEntity<Map> wishRes = restTemplate.postForEntity(baseUrl + "/wishlist", wishEntity, Map.class);
            assertThat(wishRes.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        }

        // 7. 본인 토큰으로 위시리스트 조회
        ResponseEntity<Map> wishListRes = restTemplate.exchange(baseUrl + "/wishlist", HttpMethod.GET, userEntity, Map.class);
        List<Map<String, Object>> wishList = (List<Map<String, Object>>) wishListRes.getBody().get("content");
        assertThat(wishListRes.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(wishList.size()).isGreaterThanOrEqualTo(3);

        // 8. 위시리스트 페이지네이션 확인 (2개씩)
        ResponseEntity<Map> wishPage1 = restTemplate.exchange(baseUrl + "/wishlist?page=0&size=2", HttpMethod.GET, userEntity, Map.class);
        ResponseEntity<Map> wishPage2 = restTemplate.exchange(baseUrl + "/wishlist?page=1&size=2", HttpMethod.GET, userEntity, Map.class);
        List<?> wishContent1 = (List<?>) wishPage1.getBody().get("content");
        List<?> wishContent2 = (List<?>) wishPage2.getBody().get("content");
        assertThat(wishContent1.size()).isLessThanOrEqualTo(2);
        assertThat(wishContent2.size()).isLessThanOrEqualTo(2);

        // 9. 정렬(예: id 내림차순)로 위시리스트 조회
        ResponseEntity<Map> wishSorted = restTemplate.exchange(baseUrl + "/wishlist?page=0&size=3&sort=id,desc", HttpMethod.GET, userEntity, Map.class);
        List<?> wishSortedContent = (List<?>) wishSorted.getBody().get("content");
        assertThat(wishSortedContent.size()).isGreaterThanOrEqualTo(1);
        // id 내림차순 정렬 확인 (id가 큰 값이 앞에 오는지)
        if (wishSortedContent.size() >= 2) {
            Map first = (Map) wishSortedContent.get(0);
            Map second = (Map) wishSortedContent.get(1);
            assertThat(((Number)first.get("id")).longValue()).isGreaterThanOrEqualTo(((Number)second.get("id")).longValue());
        }

        // 10. 위시리스트 수량 변경 (첫 번째 wish)
        // 위시리스트 목록에서 첫 번째 wish의 id, productId 추출
        ResponseEntity<Map> wishListRes2 = restTemplate.exchange(baseUrl + "/wishlist", HttpMethod.GET, userEntity, Map.class);
        List<Map<String, Object>> wishList2 = (List<Map<String, Object>>) wishListRes2.getBody().get("content");
        Map<String, Object> firstWish = wishList2.get(0);
        Long wishId = ((Number) firstWish.get("id")).longValue();
        Long productId = ((Map<String, Number>) firstWish.get("product")).get("id").longValue();

        Map<String, Object> updateReq = Map.of(
                "productId", productId,
                "quantity", 99
        );
        HttpEntity<Map<String, Object>> updateEntity = new HttpEntity<>(updateReq, userHeaders);
        ResponseEntity<Map> updateRes = restTemplate.exchange(baseUrl + "/wishlist/" + wishId, HttpMethod.PUT, updateEntity, Map.class);
        assertThat(updateRes.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(((Number) updateRes.getBody().get("quantity")).intValue()).isEqualTo(99);

        // 11. 위시리스트 삭제 (첫 번째 wish)
        ResponseEntity<Void> delRes = restTemplate.exchange(baseUrl + "/wishlist/" + wishId, HttpMethod.DELETE, userEntity, Void.class);
        assertThat(delRes.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // 12. 삭제 후 위시리스트 재조회 (삭제된 wish가 없는지 확인)
        ResponseEntity<Map> wishListRes3 = restTemplate.exchange(baseUrl + "/wishlist", HttpMethod.GET, userEntity, Map.class);
        List<Map<String, Object>> wishListAfterDel = (List<Map<String, Object>>) wishListRes3.getBody().get("content");
        boolean deletedWishExists = wishListAfterDel.stream().anyMatch(w -> ((Number)w.get("id")).longValue() == wishId);
        assertThat(deletedWishExists).isFalse();
    }
} 