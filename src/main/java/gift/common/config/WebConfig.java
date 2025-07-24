package gift.common.config;

import gift.common.security.AuthorizationAspect;
import gift.member.application.port.in.MemberUseCase;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@ConditionalOnProperty(
        name = "admin.enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class WebConfig implements WebMvcConfigurer {

    private final AuthorizationAspect authorizationAspect;
    private final MemberUseCase memberUseCase;

    public WebConfig(AuthorizationAspect authorizationAspect, MemberUseCase memberUseCase) {
        this.authorizationAspect = authorizationAspect;
        this.memberUseCase = memberUseCase;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authorizationAspect);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginMemberArgumentResolver(memberUseCase));
    }
} 