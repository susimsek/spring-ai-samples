package io.github.susimsek.springaisamples.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.Duration;

public class DurationMaxValidator implements ConstraintValidator<DurationMax, Duration> {

    private Duration maxDuration;

    @Override
    public void initialize(DurationMax annotation) {
        this.maxDuration = Duration.parse(annotation.value());
    }

    @Override
    public boolean isValid(Duration value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // null values are valid, use @NotNull for null checks
        }
        return value.compareTo(maxDuration) <= 0;
    }
}