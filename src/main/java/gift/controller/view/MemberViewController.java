package gift.controller.view;

import gift.dto.MemberRequestDto;
import gift.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin/members")
public class MemberViewController {

    private final MemberService memberService;
    public MemberViewController(MemberService memberService) {
        this.memberService = memberService;
    }

    @GetMapping
    public ModelAndView list(HttpServletRequest request) {
        Map<String, Object> model = new HashMap<>();
        model.put("members", memberService.findAll());
        return new ModelAndView("member/list", model);
    }

    @GetMapping("/new")
    public ModelAndView createForm() {
        Map<String, Object> model = new HashMap<>();
        model.put("member", new MemberRequestDto(null, null, null));
        return new ModelAndView("member/create", model);
    }

    @PostMapping
    public ModelAndView create(
            @Valid @ModelAttribute("member") MemberRequestDto requestDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            ModelAndView mav = new ModelAndView("member/create");
            mav.addObject("member", requestDto);
            return mav;
        }

        memberService.create(requestDto);
        return new ModelAndView("redirect:/admin/members");
    }

    @GetMapping("/{id}")
    public ModelAndView detail(@PathVariable Long id) {
        Map<String, Object> model = new HashMap<>();
        model.put("member", memberService.find(id));
        return new ModelAndView("member/detail", model);
    }

    @GetMapping("/edit/{id}")
    public ModelAndView editForm(@PathVariable Long id) {
        Map<String, Object> model = new HashMap<>();
        model.put("member", memberService.find(id));
        return new ModelAndView("member/edit", model);
    }

    @PostMapping("/{id}")
    public ModelAndView update(@PathVariable Long id,
                               @Valid @ModelAttribute("member") MemberRequestDto requestDto,
                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ModelAndView mav = new ModelAndView("member/edit");
            mav.addObject("member", requestDto);
            return mav;
        }
        memberService.update(id, requestDto);
        return new ModelAndView("redirect:/admin/members");
    }

    @PostMapping("/{id}/delete")
    public ModelAndView delete(@PathVariable Long id) {
        memberService.delete(id);
        return new ModelAndView("redirect:/admin/members");
    }

}
