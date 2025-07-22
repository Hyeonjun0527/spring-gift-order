package gift.validation;

import gift.dto.ProductRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ProductValidator implements ConstraintValidator<ValidProduct, ProductRequestDto> {

    @Override
    public boolean isValid(ProductRequestDto dto, ConstraintValidatorContext context) {
        boolean valid = true;

        // 커스텀 메시지를 보여주기 위해 기본 메시지 비활성화
        context.disableDefaultConstraintViolation();

        // 1. 이름 null 또는 공백 확인
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            context.buildConstraintViolationWithTemplate("상품명은 필수입니다.")
                    .addPropertyNode("name").addConstraintViolation();
            valid = false;
        }

        // 2. 길이 제한
        else if (dto.getName().length() > 15) {
            context.buildConstraintViolationWithTemplate("상품명은 최대 15자까지 입력 가능합니다.")
                    .addPropertyNode("name").addConstraintViolation();
            valid = false;
        }

        // 3. 허용 특수 문자만 포함
        else if (!dto.getName().matches("^[a-zA-Z0-9가-힣()\\[\\]+\\-&/_ ]*$")) {
            context.buildConstraintViolationWithTemplate(
                    "허용되지 않은 특수문자가 포함되어 있습니다. (허용: ( ), [ ], +, -, &, /, _)"
            ).addPropertyNode("name").addConstraintViolation();
            valid = false;
        }

        // 4. "카카오" 금지 (MD 협의 시에만 허용)
        else if (dto.getName().contains("카카오")) {
            context.buildConstraintViolationWithTemplate("상품명에 '카카오'는 MD 협의 시에만 사용할 수 있습니다.")
                    .addPropertyNode("name").addConstraintViolation();
            valid = false;
        }

        return valid;
    }
}
