package gift.common.security;

import gift.common.annotation.RequireAdmin;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.NoSuchElementException;

@Component
@ConditionalOnProperty(
    name = "jwt.enabled",
    havingValue = "true",
    matchIfMissing = true
)
public class AuthorizationAspect implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(AuthorizationAspect.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        log.info("[인터셉터] 처리 경로: {}", request.getRequestURI());
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            RequireAdmin requireAdmin = handlerMethod.getMethodAnnotation(RequireAdmin.class);
            log.info("[인터셉터] 대상 메소드: {}, @RequireAdmin 존재 여부: {}", handlerMethod.getMethod().getName(), requireAdmin != null);

            if (requireAdmin != null) {
                checkAdminPermission(request);
            }
        }
        return true;
    }

    private void checkAdminPermission(HttpServletRequest request) {
        Boolean authenticated = (Boolean) request.getAttribute("authenticated");
        String role = (String) request.getAttribute("role");
        log.info("[인터셉터] 관리자 권한 검사 시작. 인증 여부: {}, 역할: {}", authenticated, role);

        if (authenticated == null || !authenticated) {
            throw new NoSuchElementException("페이지를 찾을 수 없습니다.");
        }
        
        if (!"ADMIN".equals(role)) {
            throw new NoSuchElementException("페이지를 찾을 수 없습니다.");
        }
    }
}
