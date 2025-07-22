package gift.exception;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice(basePackages = "gift.controller.view")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ViewExceptionHandler {

    @ExceptionHandler(ProductNotExistException.class)
    public ModelAndView handleProductNotExist() {

        Map<String, String> model = new HashMap<>();
        model.put("errorMessage", "상품이 존재하지 않습니다.");

        return new ModelAndView("error/product-not-found", model);
    }


    @ExceptionHandler(MemberNotFoundException.class)
    public ModelAndView handleMemberNotFound(MemberNotFoundException ex) {
        Map<String, String> model = new HashMap<>();
        model.put("errorMessage", ex.getMessage());
        return new ModelAndView("error/member-not-found", model);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ModelAndView handleInvalidPassword() {
        Map<String, String> model = new HashMap<>();
        model.put("errorMessage", "비밀번호가 일치하지 않습니다.");
        return new ModelAndView("error/invalid-password", model);
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ModelAndView handleUnauthorizedAccess(UnauthorizedAccessException ex) {
        Map<String, String> model = new HashMap<>();
        model.put("errorMessage", ex.getMessage());
        return new ModelAndView("error/unauthorized", model);
    }
}
