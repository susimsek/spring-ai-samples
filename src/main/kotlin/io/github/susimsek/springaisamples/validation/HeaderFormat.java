package io.github.susimsek.springaisamples.validation;


import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = HeaderValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface HeaderFormat {
    String message() default "Invalid header";
    String headerName();
    boolean notBlank() default true;
    int min() default 0;
    int max() default Integer.MAX_VALUE;
    String regexp() default "";
    String blankMessage() default "{validation.field.notBlank}";
    String sizeMessage() default "{validation.field.size}";
    String patternMessage() default "{validation.field.pattern}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}