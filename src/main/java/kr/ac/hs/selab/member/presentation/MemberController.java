package kr.ac.hs.selab.member.presentation;

import kr.ac.hs.selab.common.dto.AuthUser;
import kr.ac.hs.selab.member.application.MemberService;
import kr.ac.hs.selab.member.dto.MemberPrivacyDto;
import kr.ac.hs.selab.member.dto.MemberSignUpDto;
import kr.ac.hs.selab.oauth.dto.CustomOAuth2User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;

@RequiredArgsConstructor
@Controller
@RequestMapping("auth")
public class MemberController {
    private final MemberService memberService;

    @GetMapping("login")
    public String login() {
        return "/fragments/member/login";
    }

    @GetMapping("signup/basic")
    public String signupBasic() {
        return "/fragments/member/signup-basic";
    }

    @PostMapping("signup/basic")
    public String signupBasic(@Valid MemberSignUpDto memberSignUpDto) {
        memberService.create(memberSignUpDto);
        return "redirect:/auth/login";
    }

    @GetMapping("signup/social")
    public String signupSocial() {
        return "fragments/member/signup-social";
    }

    @PostMapping("signup/social")
    public String signupSocial(@AuthenticationPrincipal CustomOAuth2User customOAuth2User,
                               @Valid MemberPrivacyDto memberPrivacyDto) {
        memberService.updateSocialInfo(customOAuth2User.getId(), memberPrivacyDto);
        return "fragments/index";
    }

    @GetMapping("edit/privacy")
    public String editPrivacy() {
        return "fragments/member/edit-privacy";
    }

    @PostMapping("edit/privacy")
    public String editPrivacy(@AuthenticationPrincipal AuthUser authUser,
                              @Valid MemberPrivacyDto memberPrivacyDto) {
        memberService.updatePrivacy(authUser.getId(), memberPrivacyDto);
        return "fragments/index";
    }
}