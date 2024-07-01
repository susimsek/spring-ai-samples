package io.github.susimsek.springaisamples.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.hibernate.validator.internal.constraintvalidators.hv.time.DurationMaxValidator;

@Documented
@Constraint(validatedBy = DurationMaxValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface DurationMax {
    String value();
    String message() default "{validation.field.max}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}