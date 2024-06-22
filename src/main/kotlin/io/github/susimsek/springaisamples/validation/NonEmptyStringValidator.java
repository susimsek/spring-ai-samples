package io.github.susimsek.springaisamples.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;


public class NonEmptyStringValidator implements ConstraintValidator<NonEmptyString, Object> {

    // Default constructor
    public NonEmptyStringValidator() {
    }

    @Override
    public void initialize(NonEmptyString constraintAnnotation) {
        // This method is intentionally left empty because this validator does not require any initialization
        // If any initialization logic is needed in the future, it can be added here
    }

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