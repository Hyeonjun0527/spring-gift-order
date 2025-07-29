package gift.common.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class ClientConfig {

    @Bean
    @Primary
    public RestClient restClient(RestTemplateBuilder builder) {
        return RestClient.create(builder.build());
    }

    @Bean
    public RestClient kakaoRestClient(RestTemplateBuilder builder) {
        RestTemplate restTemplate = builder
                .connectTimeout(Duration.ofSeconds(3))
                .readTimeout(Duration.ofSeconds(5))
            .build();
        return RestClient.create(restTemplate);
    }
}