package gift;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MemberControllerTest {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate restTemplate;

    String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port + "/api/members";
    }

    @Test
    @DisplayName("회원가입 성공")
    void register_success() {
        String email = "test" + UUID.randomUUID().toString().substring(0, 6) + "@test.com";
        Map<String, String> req = Map.of("email", email, "password", "123456");

        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/register", req, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).contains("token");
    }

    @Test
    @DisplayName("회원가입 실패 - 중복 이메일")
    void register_fail_duplicateEmail() {
        String email = "dup@test.com";
        Map<String, String> req = Map.of("email", email, "password", "123456");

        // 첫 가입 성공
        restTemplate.postForEntity(baseUrl + "/register", req, String.class);
        // 두 번째 가입 → 실패
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/register", req, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("이미 존재하는 이메일입니다.");
    }

    @Test
    @DisplayName("로그인 성공")
    void login_success() {
        String email = "login" + UUID.randomUUID().toString().substring(0, 6) + "@test.com";
        Map<String, String> registerReq = Map.of("email", email, "password", "123456");
        restTemplate.postForEntity(baseUrl + "/register", registerReq, String.class);

        Map<String, String> loginReq = Map.of("email", email, "password", "123456");
        ResponseEntity<Map> response = restTemplate.postForEntity(baseUrl + "/login", loginReq, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsKey("token");
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void login_fail_wrongPassword() {
        String email = "wrongpw" + UUID.randomUUID().toString().substring(0, 6) + "@test.com";
        Map<String, String> registerReq = Map.of("email", email, "password", "123456");
        restTemplate.postForEntity(baseUrl + "/register", registerReq, String.class);

        Map<String, String> loginReq = Map.of("email", email, "password", "wrongpw");
        ResponseEntity<String> response = restTemplate.postForEntity(baseUrl + "/login", loginReq, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).contains("비밀번호가 일치하지 않습니다");
    }
}
