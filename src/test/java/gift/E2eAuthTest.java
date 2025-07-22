package gift;

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
class E2eAuthTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void 회원가입_로그인_인증_전체_플로우_테스트() {
        String baseUrl = "http://localhost:" + port;

        // 1. 회원가입 (매번 다른 이메일 사용)
        String uniqueEmail = "testuser" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
        Map<String, String> registerRequest = Map.of(
            "email", uniqueEmail,
            "password", "123456"
        );
        ResponseEntity<String> registerResponse = restTemplate.postForEntity(
            baseUrl + "/api/members/register", registerRequest, String.class
        );
        assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        String token = registerResponse.getBody().split("\"token\":\"")[1].split("\"")[0];
        assertThat(token).isNotBlank();

        // 2. 토큰 없이 인증이 필요한 API 호출 → 401
        ResponseEntity<String> noAuthResponse = restTemplate.getForEntity(
            baseUrl + "/admin/members", String.class
        );
        assertThat(noAuthResponse.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);

        // 3. 일반 사용자 토큰으로 관리자 API 호출 → 403 (권한 부족)
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<String> authResponse = restTemplate.exchange(
            baseUrl + "/admin/members", HttpMethod.GET, entity, String.class
        );
        assertThat(authResponse.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void 관리자_권한_테스트() {
        String baseUrl = "http://localhost:" + port;

        // 1. 관리자 계정으로 로그인 (기본 관리자 계정 사용)
        Map<String, String> loginRequest = Map.of(
            "email", "admin@admin",
            "password", "admin123"
        );
        ResponseEntity<String> loginResponse = restTemplate.postForEntity(
            baseUrl + "/api/members/login", loginRequest, String.class
        );
        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        String adminToken = loginResponse.getBody().split("\"token\":\"")[1].split("\"")[0];
        assertThat(adminToken).isNotBlank();

        // 2. 관리자 토큰으로 관리자 API 호출 → 200 (성공)
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + adminToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<String> adminResponse = restTemplate.exchange(
            baseUrl + "/admin/members", HttpMethod.GET, entity, String.class
        );
        assertThat(adminResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
} 