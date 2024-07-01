package io.github.susimsek.springaisamples.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.unit.DataSize;

public class DataSizeMaxValidator implements ConstraintValidator<DataSizeMax, DataSize> {

    private DataSize maxSize;

    @Override
    public void initialize(DataSizeMax annotation) {
        this.maxSize = DataSize.parse(annotation.value());
    }

    @Override
    public boolean isValid(DataSize value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // null values are valid, use @NotNull for null checks
        }
        return value.toBytes() <= maxSize.toBytes();
    }
}