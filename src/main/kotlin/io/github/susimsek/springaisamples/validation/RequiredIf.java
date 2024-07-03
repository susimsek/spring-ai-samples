package io.github.susimsek.springaisamples.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = RequiredIfValidator.class)
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiredIf {
    String message() default "{validation.field.notBlank}";
    String notNullMessage() default "{validation.field.notNull}";
    String notBlankMessage() default "{validation.field.notBlank}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String conditionField();
    String conditionValue();
    String requiredField();
}