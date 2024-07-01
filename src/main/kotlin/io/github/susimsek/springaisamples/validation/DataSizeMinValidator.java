package io.github.susimsek.springaisamples.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.unit.DataSize;

public class DataSizeMinValidator implements ConstraintValidator<DataSizeMin, DataSize> {

    private DataSize minSize;

    @Override
    public void initialize(DataSizeMin annotation) {
        this.minSize = DataSize.parse(annotation.value());
    }

    @Override
    public boolean isValid(DataSize value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // null values are valid, use @NotNull for null checks
        }
        return value.toBytes() >= minSize.toBytes();
    }
}