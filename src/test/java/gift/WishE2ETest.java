package gift;

import gift.dto.*;
import gift.utils.E2ETestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.List;

import static java.util.List.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WishE2ETest {

    @LocalServerPort
    private int port;

    private RestClient restClient;
    String token;

    @BeforeEach
    void setUp() {
        restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port)
                .build();

        token = new E2ETestUtils(restClient).회원가입_후_토큰_발급();
    }

    @Test
    void 위시리스트_추가_및_조회_삭제() {

        // 상품 등록
        List<OptionRequestDto> options = of(
                new OptionRequestDto("테스트 옵션", 100)
        );
        ProductRequestDto productRequest = new ProductRequestDto("아이스 아메리카노", 4500, "ice_americano.jpg", options);

        ProductResponseDto productResponse = restClient.post()
                .uri("/api/products")
                .header("Authorization", "Bearer " + token)
                .body(productRequest)
                .retrieve()
                .body(ProductResponseDto.class);

        assertThat(productResponse).isNotNull();

        // 위시 리스트에 상품 추가
        WishRequestDto wishRequest = new WishRequestDto(productResponse.id());

        WishCreateResponseDto wishResponse = restClient.post()
                .uri("/api/wishes")
                .header("Authorization", "Bearer " + token)
                .body(wishRequest)
                .retrieve()
                .body(WishCreateResponseDto.class);

        assertThat(wishResponse.productId()).isEqualTo(productResponse.id());

        // 위시 리스트 조회
        List<WishResponseDto> wishlist = restClient.get()
                .uri("/api/wishes")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(new ParameterizedTypeReference<List<WishResponseDto>>() {});

        assertThat(wishlist.get(0).product().name()).isEqualTo("아이스 아메리카노");

        // 위시 리스트에서 제거
        restClient.delete()
                .uri("/api/wishes/" + wishResponse.id())
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toBodilessEntity();

        // 삭제 후 위시 리스트가 비었는지 확인
        List<WishResponseDto> afterDelete = restClient.get()
                .uri("/api/wishes")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .body(new ParameterizedTypeReference<List<WishResponseDto>>() {});

        assertThat(afterDelete).isEmpty();
    }

    private String 회원가입_후_토큰_발급() {
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

    @Test
    void 존재하지_않는_상품으로_위시_추가_시_실패() {
        // given
        WishRequestDto request = new WishRequestDto(999999L);

        // when
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            restClient.post()
                    .uri("/api/wishes")
                    .header("Authorization", "Bearer " + token)
                    .body(request)
                    .retrieve()
                    .body(WishCreateResponseDto.class);
        });

        // then
        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void 중복된_상품_위시_추가_시_실패() {
        // given
        List<OptionRequestDto> options = of(
                new OptionRequestDto("테스트 옵션", 100)
        );
        ProductRequestDto productRequest = new ProductRequestDto("카페라떼", 4800, "latte.jpg", options);

        ProductResponseDto product = restClient.post()
                .uri("/api/products")
                .header("Authorization", "Bearer " + token)
                .body(productRequest)
                .retrieve()
                .body(ProductResponseDto.class);

        WishRequestDto wishRequest = new WishRequestDto(product.id());

        restClient.post()
                .uri("/api/wishes")
                .header("Authorization", "Bearer " + token)
                .body(wishRequest)
                .retrieve()
                .toBodilessEntity();

        // when
        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            restClient.post()
                    .uri("/api/wishes")
                    .header("Authorization", "Bearer " + token)
                    .body(wishRequest)
                    .retrieve()
                    .body(WishCreateResponseDto.class);
        });

        // then
        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    }
}