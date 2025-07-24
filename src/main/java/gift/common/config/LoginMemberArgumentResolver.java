package gift.common.config;

import gift.common.annotation.LoginMember;
import gift.member.application.port.in.MemberUseCase;
import gift.member.domain.model.Member;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Objects;

public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final MemberUseCase memberUseCase;

    public LoginMemberArgumentResolver(MemberUseCase memberUseCase) {
        this.memberUseCase = memberUseCase;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginMember.class) &&
                parameter.getParameterType().equals(Member.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        Long memberId = (Long) Objects.requireNonNull(request).getAttribute("memberId");

        if (memberId == null) {
            throw new IllegalArgumentException("인증된 사용자 정보를 찾을 수 없습니다.");
        }

        return memberUseCase.getMemberById(memberId);
    }
}