package io.github.susimsek.springaisamples.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.Duration;

public class DurationMinValidator implements ConstraintValidator<DurationMin, Duration> {

    private Duration minDuration;

    @Override
    public void initialize(DurationMin annotation) {
        this.minDuration = Duration.parse(annotation.value());
    }

    @Override
    public boolean isValid(Duration value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // null values are valid, use @NotNull for null checks
        }
        return value.compareTo(minDuration) >= 0;
    }
}