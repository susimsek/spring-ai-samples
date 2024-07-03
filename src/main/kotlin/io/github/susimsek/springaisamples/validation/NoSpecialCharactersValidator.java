package io.github.susimsek.springaisamples.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class NoSpecialCharactersValidator implements ConstraintValidator<NoSpecialCharacters, String> {

    private static final String NO_SPECIAL_CHARACTERS_PATTERN = "^[a-zA-Z0-9 ]*$";

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Consider null as valid if not specified otherwise
        }
        return value.matches(NO_SPECIAL_CHARACTERS_PATTERN);
    }
}