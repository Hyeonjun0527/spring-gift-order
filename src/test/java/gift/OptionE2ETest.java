package gift;

import gift.dto.OptionRequestDto;
import gift.dto.OptionResponseDto;
import gift.dto.ProductRequestDto;
import gift.utils.E2ETestUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;

import static java.util.List.of;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class OptionE2ETest {

    @LocalServerPort
    private int port;

    private RestClient restClient;
    private String token;

    @BeforeEach
    void setUp(){
        restClient = RestClient.builder()
            .baseUrl("http://localhost:" + port)
            .build();

        token = new E2ETestUtils(restClient).회원가입_후_토큰_발급();

        List<OptionRequestDto> options = of(
                new OptionRequestDto("테스트 옵션1", 100),
                new OptionRequestDto("태스트 옵션2", 100)
        );
        ProductRequestDto productRequest = new ProductRequestDto("테스트 상품", 5000, "test.jpg", options);

        restClient.post()
                .uri("/api/products")
                .header("Authorization", "Bearer " + token)
                .body(productRequest)
                .retrieve()
                .toBodilessEntity();
    }

    @Test
    void 옵션을_추가하고_조회() {
        OptionRequestDto request = new OptionRequestDto("옵션1", 100);

        restClient.post()
                .uri("/api/products/1/options")
                .header("Authorization", "Bearer " + token)
                .body(request)
                .retrieve()
                .toBodilessEntity();

        OptionResponseDto[] response = restClient.get()
                .uri("/api/products/1/options")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(OptionResponseDto[].class);

        assertThat(response).isNotEmpty();
        OptionResponseDto option = response[response.length-1];

        assertThat(option.name()).isEqualTo("옵션1");
        assertThat(option.quantity()).isEqualTo(100);
    }

    @Test
    void 옵션을_수정하고_조회(){
        OptionRequestDto request = new OptionRequestDto("수정된 옵션", 1000);

        restClient.put()
                .uri("/api/products/1/options/1")
                .header("Authorization", "Bearer " + token)
                .body(request)
                .retrieve()
                .toBodilessEntity();

        OptionResponseDto[] response = restClient.get()
                .uri("/api/products/1/options")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(OptionResponseDto[].class);

        assertThat(response).isNotEmpty();
        OptionResponseDto option = response[0];
        assertThat(option.name()).isEqualTo("수정된 옵션");
        assertThat(option.quantity()).isEqualTo(1000);
    }

    @Test
    void 옵션을_삭제() {
        restClient.delete()
                .uri("/api/products/1/options/1")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toBodilessEntity();

        OptionResponseDto[] response = restClient.get()
                .uri("/api/products/1/options")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(OptionResponseDto[].class);

        assertThat(response.length).isEqualTo(1);
    }

    @Test
    void 옵션_등록_유효성_검사_실패() {
        OptionRequestDto invalidRequest = new OptionRequestDto("테스트 옵션!테스트 옵션!테스트 옵션!테스트 옵션!테스트 옵션!테스트 옵션!테스트 옵션!테스트 옵션!테스트 옵션!테스트 옵션!", 0);

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            restClient.post()
                    .uri("/api/products/1/options")
                    .header("Authorization", "Bearer " + token)
                    .body(invalidRequest)
                    .retrieve()
                    .toBodilessEntity();
        });

        Assertions.assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        String responseBody = exception.getResponseBodyAsString();

        Assertions.assertThat(responseBody).contains("Size");
        Assertions.assertThat(responseBody).contains("Pattern");
        Assertions.assertThat(responseBody).contains("Min");
    }

    @Test
    void 옵션명_중복_검사() {
        OptionRequestDto invalidRequest = new OptionRequestDto("테스트 옵션1", 100);

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            restClient.post()
                    .uri("/api/products/1/options")
                    .header("Authorization", "Bearer " + token)
                    .body(invalidRequest)
                    .retrieve()
                    .toBodilessEntity();
        });

        Assertions.assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        String responseBody = exception.getResponseBodyAsString();

        Assertions.assertThat(responseBody).contains("존재");
    }

}
