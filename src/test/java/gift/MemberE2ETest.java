package gift;

import gift.dto.MemberLoginRequestDto;
import gift.dto.MemberLoginResponseDto;
import gift.dto.MemberRequestDto;
import gift.dto.MemberResponseDto;
import gift.utils.E2ETestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MemberE2ETest {

    @LocalServerPort
    private int port;

    private RestClient restClient;

    private String token;

    @BeforeEach
    void setUp() {
        restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();
        token = new E2ETestUtils(restClient).회원가입_후_토큰_발급();
    }

    @Test
    void 회원가입() {
        // given
        final String name = "홍길동";
        final String email = "hong" + System.currentTimeMillis() + "@email.com";
        final String password = "password";
        MemberRequestDto request = new MemberRequestDto(name, email, password);

        // when
        MemberResponseDto response = restClient.post()
                .uri("/api/members/register")
                .body(request)
                .retrieve()
                .body(MemberResponseDto.class);

        // then
        MemberLoginRequestDto loginRequest = new MemberLoginRequestDto(email, password);
        MemberLoginResponseDto loginResponse = restClient.post()
                .uri("/api/members/login")
                .body(loginRequest)
                .retrieve()
                .body(MemberLoginResponseDto.class);

        String token = loginResponse.token();

        MemberResponseDto myInfo = restClient.get()
                .uri("/api/members/myInfo")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(MemberResponseDto.class);

        assertThat(myInfo).isNotNull();
        assertThat(myInfo.name()).isEqualTo(name);
        assertThat(myInfo.email()).isEqualTo(email);
    }

    @Test
    void 로그인_성공시_JWT_토큰_반환() {
        // given
        final String name = "홍길동";
        final String email = "honggildong@email.com";
        final String password = "password";

        MemberRequestDto joinRequest = new MemberRequestDto(name, email, password);

        MemberResponseDto joinResponse = restClient.post()
                .uri("/api/members/register")
                .body(joinRequest)
                .retrieve()
                .body(MemberResponseDto.class);

        assertThat(joinResponse.id()).isNotNull();

        // when
        MemberLoginRequestDto loginRequest = new MemberLoginRequestDto(email, password);

        MemberLoginResponseDto loginResponse = restClient.post()
                .uri("/api/members/login")
                .body(loginRequest)
                .retrieve()
                .body(MemberLoginResponseDto.class);

        // then
        assertThat(loginResponse).isNotNull();
        assertThat(loginResponse.token()).isNotBlank();
    }

    @Test
    void 내정보_조회() {
        // when
        MemberResponseDto myInfo = restClient.get()
                .uri("/api/members/myInfo")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(MemberResponseDto.class);

        // then
        assertThat(myInfo).isNotNull();
        assertThat(myInfo.name()).isEqualTo("홍길동");
    }

    @Test
    void 내정보_수정() {

        MemberRequestDto updateRequest = new MemberRequestDto("이순신", "lee@email.com", "new-password");

        // when
        MemberResponseDto updatedInfo = restClient.put()
                .uri("/api/members/myInfo")
                .header("Authorization", "Bearer " + token)
                .body(updateRequest)
                .retrieve()
                .body(MemberResponseDto.class);

        // then
        assertThat(updatedInfo.name()).isEqualTo("이순신");
        assertThat(updatedInfo.email()).isEqualTo("lee@email.com");
    }

    @Test
    void 내정보_삭제() {
        // when
        var deleteResponse = restClient.delete()
                .uri("/api/members/withdraw")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toBodilessEntity();

        // then
        assertThat(deleteResponse.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    void 존재하지_않는_이메일로_로그인_시_404() {
        //given
        MemberLoginRequestDto loginRequest = new MemberLoginRequestDto("not_exist@email.com", "password");

        //when
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            restClient.post()
                    .uri("/api/members/login")
                    .body(loginRequest)
                    .retrieve()
                    .body(MemberLoginResponseDto.class);
        });

        //then
        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void 틀린_비밀번호로_로그인_시_401() {
        //given
        String email = "test" + System.currentTimeMillis() + "@email.com";
        String password = "password";

        restClient.post()
                .uri("/api/members/register")
                .body(new MemberRequestDto("사용자", email, password))
                .retrieve()
                .toBodilessEntity();

        //when
        MemberLoginRequestDto loginRequest = new MemberLoginRequestDto(email, "wrong-password");

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            restClient.post()
                    .uri("/api/members/login")
                    .body(loginRequest)
                    .retrieve()
                    .body(MemberLoginResponseDto.class);
        });

        //then
        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void 토큰없이_내정보_조회시_401() {
        //when
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            restClient.get()
                    .uri("/api/members/myInfo")
                    .retrieve()
                    .body(MemberResponseDto.class);
        });

        //then
        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

}
