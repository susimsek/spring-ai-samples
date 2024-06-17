package io.github.susimsek.springaisamples.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

public class NonEmptyStringValidator implements ConstraintValidator<NonEmptyString, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        if (value instanceof String stringValue) {
            return StringUtils.hasText(stringValue);
        }
        return true;
    }
}