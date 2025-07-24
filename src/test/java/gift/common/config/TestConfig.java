package gift.common.config;

import gift.common.jwt.JwtTokenPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@TestConfiguration
public class TestConfig {

    @Bean
    public JwtTokenPort jwtTokenPort() {
        return new MockJwtTokenProvider();
    }

    @Configuration
    @ConditionalOnProperty(
            name = "admin.enabled",
            havingValue = "true"
    )
    public static class WebTestConfig implements WebMvcConfigurer {
    }
}