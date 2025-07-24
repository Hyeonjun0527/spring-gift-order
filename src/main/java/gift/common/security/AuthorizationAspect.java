package gift.common.security;

import gift.common.annotation.RequireAdmin;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            RequireAdmin requireAdmin = handlerMethod.getMethodAnnotation(RequireAdmin.class);

            if (requireAdmin != null) {
                checkAdminPermission(request);
            }
        }
        return true;
    }

    private void checkAdminPermission(HttpServletRequest request) {
        Boolean authenticated = (Boolean) request.getAttribute("authenticated");
        if (authenticated == null || !authenticated) {
            throw new NoSuchElementException("페이지를 찾을 수 없습니다.");
        }
        
        String role = (String) request.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            throw new NoSuchElementException("페이지를 찾을 수 없습니다.");
        }
    }
}
