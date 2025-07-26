package gift.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.common.security.PasswordEncoder;
import gift.member.application.port.in.dto.KakaoTokenResponse;
import gift.member.application.port.in.dto.KakaoUserInfoResponse;
import gift.member.application.port.in.dto.LoginRequest;
import gift.member.application.port.in.dto.RegisterRequest;
import gift.member.domain.model.Member;
import gift.member.domain.model.Role;
import gift.common.jwt.JwtTokenPort;
import gift.member.domain.port.out.MemberRepository;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(properties = {"jwt.enabled=true", "admin.enabled=true"})
public class MemberIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenPort jwtTokenPort;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private static MockWebServer mockWebServer;

    @BeforeAll
    static void beforeAll() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
    }

    @AfterAll
    static void afterAll() throws IOException {
        mockWebServer.shutdown();
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("kakao.token-uri", () -> mockWebServer.url("/oauth/token").toString());
        registry.add("kakao.user-info-uri", () -> mockWebServer.url("/v2/user/me").toString());
    }

    @BeforeEach
    void setUp() {
        memberRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        // MockWebServer 정리는 @AfterAll에서 처리하므로 여기서는 할 일이 없습니다.
    }

    @Nested
    @DisplayName("카카오 로그인")
    class KakaoLoginTest {
        @Test
        @DisplayName("카카오 로그인 성공 - 신규 회원")
        void kakaoLoginSuccess_NewMember() throws Exception {
            // given
            String authCode = "test_auth_code";
            KakaoTokenResponse tokenResponse = new KakaoTokenResponse("bearer", "test_access_token", 60, "test_refresh_token", 120, "profile_nickname");
            KakaoUserInfoResponse userInfoResponse = new KakaoUserInfoResponse(12345L, "connected_at", Map.of("nickname", "testuser"),
                    new KakaoUserInfoResponse.KakaoAccount(new KakaoUserInfoResponse.Profile("testuser"), "test@kakao.com"));

            mockWebServer.enqueue(new MockResponse()
                    .setBody(objectMapper.writeValueAsString(tokenResponse))
                    .addHeader("Content-Type", "application/json"));
            mockWebServer.enqueue(new MockResponse()
                    .setBody(objectMapper.writeValueAsString(userInfoResponse))
                    .addHeader("Content-Type", "application/json"));

            // when & then
            mockMvc.perform(get("/api/members/kakao/callback").param("code", authCode))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").exists());

            assertThat(memberRepository.findByKakaoId(12345L)).isPresent();
        }

        @Test
        @DisplayName("카카오 로그인 실패 - 토큰 발급 4xx 에러")
        void kakaoLoginFail_Token4xxError() throws Exception {
            // given
            String authCode = "invalid_auth_code";
            mockWebServer.enqueue(new MockResponse().setResponseCode(400).setBody("{\"error\":\"invalid_grant\"}"));

            // when & then
            mockMvc.perform(get("/api/members/kakao/callback").param("code", authCode))
                    .andExpect(status().isInternalServerError()); // RestControllerAdvice가 없어 500으로 처리됨
        }
    }

    @Nested
    @DisplayName("일반 회원가입 및 로그인")
    class RegisterAndLoginTest {

        @Test
        @DisplayName("성공 - 정상적인 회원가입 후 로그인")
        void registerAndLogin_Success() throws Exception {
            // given
            RegisterRequest registerRequest = new RegisterRequest("test@example.com", "password123");

            // when & then
            mockMvc.perform(post("/api/members/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(registerRequest)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.token").exists());

            LoginRequest loginRequest = new LoginRequest("test@example.com", "password123");
            mockMvc.perform(post("/api/members/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").exists());
        }

        @Test
        @DisplayName("실패 - 중복된 이메일로 회원가입")
        void register_Fail_DuplicateEmail() throws Exception {
            Member existingMember = Member.of(null, "test@example.com", passwordEncoder.encode("any-password"), Role.USER, null, null);
            memberRepository.save(existingMember);

            RegisterRequest request = new RegisterRequest("test@example.com", "password123");

            // when & then
            mockMvc.perform(post("/api/members/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("실패 - 잘못된 비밀번호로 로그인")
        void login_Fail_WrongPassword() throws Exception {
            // given
            RegisterRequest registerRequest = new RegisterRequest("test@example.com", "password123");
            mockMvc.perform(post("/api/members/register")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(registerRequest)));

            LoginRequest loginRequest = new LoginRequest("test@example.com", "wrongPassword");

            // when & then
            mockMvc.perform(post("/api/members/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(loginRequest)))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("관리자 기능")
    class AdminFunctionTest {

        private String adminToken;
        private String userToken;
        private Member user;

        @BeforeEach
        void setUp() {
            memberRepository.deleteAll();
            Member admin = memberRepository.save(Member.of(null, "admin@test.com", passwordEncoder.encode("adminpass"), Role.ADMIN, null, null));
            user = memberRepository.save(Member.of(null, "user@test.com", passwordEncoder.encode("userpass"), Role.USER, null, null));

            adminToken = "Bearer " + jwtTokenPort.createAccessToken(admin.id(), admin.email(), admin.role());
            userToken = "Bearer " + jwtTokenPort.createAccessToken(user.id(), user.email(), user.role());
        }

        @Test
        @DisplayName("성공 - 관리자가 모든 회원 목록을 조회")
        void getAllMembers_ByAdmin() throws Exception {
            mockMvc.perform(get("/api/admin/members")
                            .header("Authorization", adminToken))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(2));
        }

        @Test
        @DisplayName("실패 - 일반 사용자가 모든 회원 목록을 조회")
        void getAllMembers_ByUser() throws Exception {
            mockMvc.perform(get("/api/admin/members")
                            .header("Authorization", userToken))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("성공 - 관리자가 회원 정보를 삭제")
        void deleteMember_ByAdmin() throws Exception {
            mockMvc.perform(delete("/api/admin/members/" + user.id())
                            .header("Authorization", adminToken))
                    .andExpect(status().isNoContent());

            assertThat(memberRepository.findById(user.id())).isEmpty();
        }
    }
} 