package gift.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ForbiddenWordValidator implements ConstraintValidator<ForbiddenWord, String> {

    private String forbidden;

    @Override
    public void initialize(ForbiddenWord constraintAnnotation) {
        this.forbidden = constraintAnnotation.word();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) { return true; }
        return !value.contains(forbidden);
    }
}
