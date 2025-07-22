package gift.common.config;

import gift.common.jwt.JwtTokenPort;
import gift.common.jwt.JwtTokenProvider;
import gift.member.domain.model.Role;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class MockJwtTokenProvider implements JwtTokenPort {

    @Override
    public String createAccessToken(Long memberId, String email, Role role) {
        return "test-token";
    }

    @Override
    public JwtTokenProvider.TokenValidationResult validateToken(String token) {
        return JwtTokenProvider.TokenValidationResult.valid(null);
    }

    @Override
    public String getEmailFromToken(String token) {
        return "test@example.com";
    }

    @Override
    public Long getMemberIdFromToken(String token) {
        return 1L;
    }

    @Override
    public String getTokenTypeFromToken(String token) {
        return "access";
    }

    @Override
    public String getRoleFromToken(String token) {
        return "USER";
    }

    @Override
    public String resolveToken(HttpServletRequest request) {
        return null;
    }
} 