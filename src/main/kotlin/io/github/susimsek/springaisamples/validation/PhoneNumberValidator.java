package io.github.susimsek.springaisamples.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class PhoneNumberValidator implements ConstraintValidator<PhoneNumber, String> {

    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9. ()-]{7,25}$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Consider null as valid if not specified otherwise
        }
        return PHONE_PATTERN.matcher(value).matches();
    }
}