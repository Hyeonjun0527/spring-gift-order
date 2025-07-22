package gift.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ForbiddenWordValidator.class)
@Documented
public @interface ForbiddenWord {
    String message() default "'카카오'가 포함된 상품은 담당 MD에게 문의해주세요.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String word();
}
