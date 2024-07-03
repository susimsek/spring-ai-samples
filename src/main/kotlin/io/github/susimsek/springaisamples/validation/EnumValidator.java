package io.github.susimsek.springaisamples.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;


public class EnumValidator implements ConstraintValidator<Enum, Object> {

    private Set<String> allowedValues;
    private String message;

    @Override
    public void initialize(Enum annotation) {
        allowedValues = Arrays.stream(annotation.enumClass().getEnumConstants())
            .map(java.lang.Enum::name)
            .collect(Collectors.toSet());
        this.message = annotation.message();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // null values are valid, use @NotNull for null checks
        }

        boolean isValid = value instanceof String stringValue
            ? allowedValues.contains(stringValue)
            : value instanceof java.lang.Enum<?> enumValue && allowedValues.contains(enumValue.name());

        if (!isValid) {
            String allowedValuesString = String.join(", ", allowedValues);

            HibernateConstraintValidatorContext hibernateContext = context.unwrap(
                HibernateConstraintValidatorContext.class);
            hibernateContext.disableDefaultConstraintViolation();
            hibernateContext.addMessageParameter("value", value);
            hibernateContext.addMessageParameter("allowedValues", allowedValuesString);
            hibernateContext.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
        }

        return isValid;
    }
}