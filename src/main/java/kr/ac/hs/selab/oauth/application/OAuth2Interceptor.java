package kr.ac.hs.selab.oauth.application;

import kr.ac.hs.selab.exception.ErrorMessage;
import kr.ac.hs.selab.exception.InvalidLoginException;
import kr.ac.hs.selab.member.domain.Member;
import kr.ac.hs.selab.member.infrastructure.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Configuration
@RequiredArgsConstructor
public class OAuth2Interceptor implements HandlerInterceptor {
    private final MemberRepository memberRepository;

    private static final String SOCIAL_SIGN_UP_URL = "http://localhost:8080/auth/signup/social";

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object object,
                           ModelAndView modelAndView) throws IOException {
        if (!isNullPrincipal(request) && isMemberPrivacyEmpty(request)) {
            response.sendRedirect(SOCIAL_SIGN_UP_URL);
        }
    }

    private boolean isNullPrincipal(HttpServletRequest request) {
        return Objects.isNull(request.getUserPrincipal());
    }

    private boolean isMemberPrivacyEmpty(HttpServletRequest request) {
        return memberRepository.findByEmail(request.getUserPrincipal().getName())
                .orElseThrow(() -> new InvalidLoginException(ErrorMessage.NON_EXISTENT_USER_PRIVACY))
                .checkPrivacyEmpty();
    }
}
