package gift.common.jwt;

import gift.member.domain.model.Role;
import jakarta.servlet.http.HttpServletRequest;

public interface JwtTokenPort {
    String createAccessToken(Long memberId, String email, Role role);
    JwtTokenProvider.TokenValidationResult validateToken(String token);
    String getEmailFromToken(String token);
    Long getMemberIdFromToken(String token);
    String getTokenTypeFromToken(String token);
    String getRoleFromToken(String token);
    String resolveToken(HttpServletRequest request);
}
