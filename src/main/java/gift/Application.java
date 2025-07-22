package gift;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import gift.config.JwtAuthFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Configuration
    @Profile("!test")
    public class FilterConfig {
        @Bean
        public FilterRegistrationBean<JwtAuthFilter> jwtAuthFilterRegistration(JwtAuthFilter jwtAuthFilter) {
            FilterRegistrationBean<JwtAuthFilter> registrationBean = new FilterRegistrationBean<>();
            registrationBean.setFilter(jwtAuthFilter);
            registrationBean.addUrlPatterns("/*");
            return registrationBean;
        }
    }
}
