package gift.jwt;

import gift.exception.UnauthorizedAccessException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtAuthInterceptor implements HandlerInterceptor {

    private final JwtProvider jwtProvider;
    private static final String BEARER_PREFIX = "Bearer ";

    public JwtAuthInterceptor(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("jwt".equals(cookie.getName())) {
                        authHeader = BEARER_PREFIX + cookie.getValue();
                        break;
                    }
                }
            }
        }

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            throw new UnauthorizedAccessException("인증 토큰이 없습니다.");
        }

        try {
            String token = authHeader.substring(BEARER_PREFIX.length());
            Claims claims = jwtProvider.parseClaims(token);

            if (request.getRequestURI().startsWith("/admin")) {
                String role = claims.get("role", String.class);
                if (!"ADMIN".equals(role)) {
                    throw new UnauthorizedAccessException("관리자 권한이 필요합니다.");
                }
            }

            request.setAttribute("memberId", claims.getSubject());
            return true;

        } catch (ExpiredJwtException e) {
            throw new UnauthorizedAccessException("만료된 토큰입니다.");
        } catch (JwtException | IllegalArgumentException e) {
            throw new UnauthorizedAccessException("유효하지 않은 토큰입니다.");
        }
    }
}