package gift.member.application.port.out;

import gift.member.application.port.in.dto.KakaoTokenResponse;
import gift.member.application.port.in.dto.KakaoUserInfoResponse;

public interface KakaoAuthPort {
    KakaoTokenResponse fetchToken(String authCode);
    KakaoUserInfoResponse fetchUserInfo(String accessToken);
} 