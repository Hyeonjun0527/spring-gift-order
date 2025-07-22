package gift.config;

import gift.entity.Member;
import gift.entity.MemberRole;
import gift.repository.MemberRepository;
import gift.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final MemberRepository memberRepository;

    public JwtAuthFilter(JwtService jwtService, MemberRepository memberRepository) {
        this.jwtService = jwtService;
        this.memberRepository = memberRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = jwtService.extractTokenFromBearer(authHeader);

        // 특정 URI만 필터링
        if (requiresAuth(request.getRequestURI())) {
            if (!StringUtils.hasText(token) || !jwtService.validateToken(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 에러 발생
                response.setContentType("application/json; charset=UTF-8");
                response.getWriter().write("{\"message\": \"인증이 필요합니다.\"}");
                return;
            }

            // 토큰에서 회원 ID 추출
            Long memberId = jwtService.extractMemberId(token);
            String role = jwtService.extractRole(token);

            // DB에서 Member 조회
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
            member.setRole(MemberRole.valueOf(role));
            request.setAttribute("loginMember", member);



        }

        filterChain.doFilter(request, response);
    }

    private boolean requiresAuth(String uri) {
        return uri.startsWith("/api/products") || uri.startsWith("/admin") || uri.startsWith("/wishlist");
    }
}
