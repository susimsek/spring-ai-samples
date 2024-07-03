package io.github.susimsek.springaisamples.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = DateRangeValidator.class)
@Target({ ElementType.TYPE, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface DateRange {
    String message() default "{validation.field.dateRange}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};

    String startDateField();
    String endDateField();
}