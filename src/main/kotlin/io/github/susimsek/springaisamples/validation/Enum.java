package io.github.susimsek.springaisamples.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = EnumValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface Enum {
    Class<? extends java.lang.Enum<?>> enumClass();
    String message() default "{validation.field.enum}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}