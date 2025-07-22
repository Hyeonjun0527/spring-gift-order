package gift.controller.view;

import gift.dto.MemberLoginRequestDto;
import gift.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminViewController {

    private final MemberService memberService;
    public AdminViewController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping("/login")
    public ModelAndView login() {
        Map<String, Object> model = new HashMap<>();
        model.put("admin", new MemberLoginRequestDto(null, null));
        return new ModelAndView("admin/login", model);
    }

    @PostMapping("/login")
    public ModelAndView login(
            @Valid @ModelAttribute("admin") MemberLoginRequestDto requestDto,
            BindingResult bindingResult,
            HttpServletResponse response) {

        if (bindingResult.hasErrors()) {
            ModelAndView mav = new ModelAndView("admin/login");
            mav.addObject("admin", requestDto);
            return mav;
        }

        String token = memberService.login(requestDto);

        Cookie jwtCookie = new Cookie("jwt", token);
        jwtCookie.setPath("/");
        jwtCookie.setHttpOnly(true);
        jwtCookie.setMaxAge(60 * 60);
        response.addCookie(jwtCookie);

        return new ModelAndView("redirect:/admin/dashboard");
    }

    @GetMapping("/dashboard")
    public ModelAndView dashboard() {
        return new ModelAndView("admin/dashboard");
    }
}
