package io.github.susimsek.springaisamples.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.hibernate.validator.internal.constraintvalidators.hv.time.DurationMinValidator;

@Documented
@Constraint(validatedBy = DurationMinValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface DurationMin {
    String value();
    String message() default "{validation.field.min}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}