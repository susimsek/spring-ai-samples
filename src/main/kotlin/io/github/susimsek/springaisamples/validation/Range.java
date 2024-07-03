package io.github.susimsek.springaisamples.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = RangeValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Range {
    String message() default "{validation.field.range}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    long min() default Long.MIN_VALUE;
    long max() default Long.MAX_VALUE;
}