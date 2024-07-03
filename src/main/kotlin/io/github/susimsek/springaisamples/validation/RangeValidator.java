package io.github.susimsek.springaisamples.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RangeValidator implements ConstraintValidator<Range, Number> {

    private double min;
    private double max;

    @Override
    public void initialize(Range range) {
        this.min = range.min();
        this.max = range.max();
    }

    @Override
    public boolean isValid(Number value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        double doubleValue = value.doubleValue();
        return doubleValue >= min && doubleValue <= max;
    }
}