package io.github.susimsek.springaisamples.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.Period;

public class AdultValidator implements ConstraintValidator<Adult, LocalDate> {

    @Override
    public void initialize(Adult constraintAnnotation) {
        // Initialization if necessary
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return Period.between(value, LocalDate.now()).getYears() >= 18;
    }
}