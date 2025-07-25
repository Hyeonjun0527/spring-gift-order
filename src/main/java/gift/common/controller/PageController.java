package gift.common.controller;

import gift.common.config.KakaoProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    private final KakaoProperties kakaoProperties;

    public PageController(KakaoProperties kakaoProperties) {
        this.kakaoProperties = kakaoProperties;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("kakaoClientId", kakaoProperties.getClientId());
        model.addAttribute("kakaoRedirectUri", kakaoProperties.getRedirectUri());
        return "login";
    }

    @GetMapping("/admin")
    // @RequireAdmin
    public String admin() {
        return "admin/index";
    }
} 