package gift.utils;

import gift.dto.MemberLoginRequestDto;
import gift.dto.MemberLoginResponseDto;
import gift.dto.MemberRequestDto;
import org.springframework.web.client.RestClient;

public class E2ETestUtils {

    private final RestClient restClient;

    public E2ETestUtils(RestClient restClient) {
        this.restClient = restClient;
    }

    public String 회원가입_후_토큰_발급() {
        String name = "홍길동";
        String email = "hong" + System.currentTimeMillis() + "@email.com";
        String password = "password";

        MemberRequestDto joinRequest = new MemberRequestDto(name, email, password);

        restClient.post()
                .uri("/api/members/register")
                .body(joinRequest)
                .retrieve()
                .toBodilessEntity();

        MemberLoginRequestDto loginRequest = new MemberLoginRequestDto(email, password);

        MemberLoginResponseDto loginResponse = restClient.post()
                .uri("/api/members/login")
                .body(loginRequest)
                .retrieve()
                .body(MemberLoginResponseDto.class);

        return loginResponse.token();
    }
}
