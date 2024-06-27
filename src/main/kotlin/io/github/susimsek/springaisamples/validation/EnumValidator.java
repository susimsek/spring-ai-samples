package io.github.susimsek.springaisamples.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

public class EnumValidator implements ConstraintValidator<ValidEnum, String> {

    private Class<? extends Enum<?>> enumClass;
    private String message;

    @Override
    public void initialize(ValidEnum annotation) {
        this.enumClass = annotation.enumClass();
        this.message = annotation.message();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        boolean isValid = Arrays.stream(enumClass.getEnumConstants())
            .anyMatch(e -> e.name().equals(value));

        if (!isValid) {
            String allowedValues = Arrays.stream(enumClass.getEnumConstants())
                .map(Enum::name)
                .collect(Collectors.joining(", "));

            HibernateConstraintValidatorContext hibernateContext = context.unwrap(
                HibernateConstraintValidatorContext.class);
            hibernateContext.disableDefaultConstraintViolation();
            hibernateContext.addMessageParameter("value", value);
            hibernateContext.addMessageParameter("allowedValues", allowedValues);

            hibernateContext.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
        }

        return isValid;
    }
}