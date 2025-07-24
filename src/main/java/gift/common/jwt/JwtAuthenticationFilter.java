package gift.common.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@ConditionalOnProperty(
        name = "jwt.enabled",
        havingValue = "true",
        matchIfMissing = true
)
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final List<String> PUBLIC_PATTERNS = Arrays.asList(
            "/",
            "/login",
            "/health",
            "/actuator/**",
            "/css/**",
            "/js/**",
            "/images/**",
            "/favicon.ico",
            "/api/members/register",
            "/api/members/login",
            "/h2-console/**"
    );
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    private final JwtTokenPort jwtTokenPort;
    private final ObjectMapper objectMapper;

    public JwtAuthenticationFilter(JwtTokenPort jwtTokenPort, ObjectMapper objectMapper) {
        this.jwtTokenPort = jwtTokenPort;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        log.info("JWT 필터 처리: {}", requestURI);

        if (shouldSkipFilter(requestURI)) {
            log.info("JWT 필터 건너뜀: {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String token = jwtTokenPort.resolveToken(request);
            if (token == null) {
                String acceptHeader = request.getHeader("Accept");
                boolean isPageRequest = acceptHeader != null && acceptHeader.contains("text/html");

                if (isPageRequest && !requestURI.startsWith("/api/")) {
                    log.warn("페이지 요청에 토큰 없음. 다음 필터로 진행: {}", requestURI);
                    request.setAttribute("authenticated", false);
                    filterChain.doFilter(request, response);
                } else {
                    log.warn("API 요청에 JWT 토큰이 없음: {}", requestURI);
                    sendUnAuthenticationResponse(request, response, "인증이 필요합니다. Authorization 헤더에 JWT 토큰을 포함해주세요.");
                }
                return;
            }

            JwtTokenProvider.TokenValidationResult validationResult = jwtTokenPort.validateToken(token);

            if (validationResult.isNotValid()) {
                log.warn("유효하지 않은 JWT 토큰: {}", validationResult.getErrorMessage());
                sendUnAuthenticationResponse(request, response, validationResult.getErrorMessage());
                return;
            }

            Long memberId = jwtTokenPort.getMemberIdFromToken(token);
            String email = jwtTokenPort.getEmailFromToken(token);
            String tokenType = jwtTokenPort.getTokenTypeFromToken(token);
            String role = jwtTokenPort.getRoleFromToken(token);

            request.setAttribute("memberId", memberId);
            request.setAttribute("email", email);
            request.setAttribute("tokenType", tokenType);
            request.setAttribute("role", role);
            request.setAttribute("authenticated", true);

            log.info("JWT 인증 성공: {} (ID: {}), role: {}", email, memberId, role);

        } catch (Exception e) {
            log.error("JWT 인증 중 오류 발생", e);
            sendUnAuthenticationResponse(request, response, "인증 오류: " + e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void sendUnAuthenticationResponse(HttpServletRequest request, HttpServletResponse response, String message) throws IOException {
        String acceptHeader = request.getHeader("Accept");

        if (acceptHeader != null && acceptHeader.contains("text/html")) {
            response.sendRedirect("/login");
            return;
        }

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED, message);
        problemDetail.setTitle("Authentication Failed");

        response.getWriter().write(objectMapper.writeValueAsString(problemDetail));
    }

    private boolean shouldSkipFilter(String requestURI) {
        return PUBLIC_PATTERNS.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, requestURI));
    }
}
