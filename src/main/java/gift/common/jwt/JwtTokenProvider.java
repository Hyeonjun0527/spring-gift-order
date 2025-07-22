package gift.common.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import gift.member.domain.model.Role;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
@ConditionalOnProperty(
    name = "jwt.enabled", 
    havingValue = "true",
    matchIfMissing = true
)
public class JwtTokenProvider implements JwtTokenPort {

    private static final String BEARER_PREFIX = "Bearer ";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private final String issuer;
    private final String audience;
    private final long accessTokenExpirationMinutes;
    private final Algorithm algorithm;
    private final JWTVerifier verifier;

    public JwtTokenProvider(
            @Value("${jwt.secret:Yn2kjibddFAWtnPJ2AFlL8WXmohJMCvigQggaEypa5E=}") String secretKey,
            @Value("${jwt.issuer:spring-gift-wishlist}") String issuer,
            @Value("${jwt.audience:spring-gift-api}") String audience,
            @Value("${jwt.access-token-expiration-minutes:15}") long accessTokenExpirationMinutes) {
        this.issuer = issuer;
        this.audience = audience;
        this.accessTokenExpirationMinutes = accessTokenExpirationMinutes;
        Algorithm algo = Algorithm.HMAC256(secretKey);
        this.algorithm = algo;
        this.verifier = JWT.require(algo)
                .withIssuer(issuer)
                .withAudience(audience)
                .build();
    }

    @Override
    public String createAccessToken(Long memberId, String email, Role role) {
        Instant now = Instant.now();
        Instant expiration = now.plus(accessTokenExpirationMinutes, ChronoUnit.MINUTES);

        return JWT.create()
                .withSubject(email)
                .withClaim("memberId", memberId)
                .withClaim("email", email)
                .withClaim("role", role.name())
                .withClaim("tokenType", "access")
                .withIssuer(issuer)
                .withAudience(audience)
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(expiration))
                .sign(algorithm);
    }

    @Override
    public TokenValidationResult validateToken(String token) {
        try {
            DecodedJWT decodedJWT = verifier.verify(token);
            return TokenValidationResult.valid(decodedJWT);
        } catch (TokenExpiredException e) {
            return TokenValidationResult.expired();
        } catch (JWTVerificationException e) {
            return TokenValidationResult.invalid("토큰 검증 실패: " + e.getMessage());
        } catch (Exception e) {
            return TokenValidationResult.invalid("예상치 못한 오류: " + e.getMessage());
        }
    }

    @Override
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    @Override
    public String getEmailFromToken(String token) {
        TokenValidationResult result = validateToken(token);
        if (result.isNotValid()) {
            throw new IllegalArgumentException("유효하지 않은 토큰: " + result.getErrorMessage());
        }
        return result.getDecodedJWT().getClaim("email").asString();
    }

    @Override
    public Long getMemberIdFromToken(String token) {
        TokenValidationResult result = validateToken(token);
        if (result.isNotValid()) {
            throw new IllegalArgumentException("유효하지 않은 토큰: " + result.getErrorMessage());
        }
        return result.getDecodedJWT().getClaim("memberId").asLong();
    }

    @Override
    public String getTokenTypeFromToken(String token) {
        TokenValidationResult result = validateToken(token);
        if (result.isNotValid()) {
            throw new IllegalArgumentException("유효하지 않은 토큰: " + result.getErrorMessage());
        }
        return result.getDecodedJWT().getClaim("tokenType").asString();
    }

    @Override
    public String getRoleFromToken(String token) {
        TokenValidationResult result = validateToken(token);
        if (result.isNotValid()) {
            throw new IllegalArgumentException("유효하지 않은 토큰: " + result.getErrorMessage());
        }
        return result.getDecodedJWT().getClaim("role").asString();
    }

    public static class TokenValidationResult {
        private final boolean valid;
        private final boolean expired;
        private final String errorMessage;
        private final DecodedJWT decodedJWT;

        private TokenValidationResult(boolean valid, boolean expired, String errorMessage, DecodedJWT decodedJWT) {
            this.valid = valid;
            this.expired = expired;
            this.errorMessage = errorMessage;
            this.decodedJWT = decodedJWT;
        }

        public static TokenValidationResult valid(DecodedJWT decodedJWT) {
            return new TokenValidationResult(true, false, null, decodedJWT);
        }

        public static TokenValidationResult expired() {
            return new TokenValidationResult(false, true, "토큰이 만료되었습니다.", null);
        }

        public static TokenValidationResult invalid(String errorMessage) {
            return new TokenValidationResult(false, false, errorMessage, null);
        }

        public boolean isNotValid() { return !valid; }
        public String getErrorMessage() { return errorMessage; }
        public DecodedJWT getDecodedJWT() { return decodedJWT; }
    }
} 