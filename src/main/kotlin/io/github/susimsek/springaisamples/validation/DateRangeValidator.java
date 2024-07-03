package io.github.susimsek.springaisamples.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;

public class DateRangeValidator implements ConstraintValidator<DateRange, Object> {

    private String startDateField;
    private String endDateField;

    @Override
    public void initialize(DateRange constraintAnnotation) {
        this.startDateField = constraintAnnotation.startDateField();
        this.endDateField = constraintAnnotation.endDateField();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        BeanWrapperImpl beanWrapper = new BeanWrapperImpl(value);
        Object startDateValue = beanWrapper.getPropertyValue(startDateField);
        Object endDateValue = beanWrapper.getPropertyValue(endDateField);

        if (startDateValue == null || endDateValue == null) {
            return true;
        }

        if (!(startDateValue instanceof Comparable) || !(endDateValue instanceof Comparable)) {
            throw new IllegalArgumentException(
                "Fields are not of type Comparable: " + startDateField + ", " + endDateField);
        }

        @SuppressWarnings("unchecked")
        Comparable<Object> startDate = (Comparable<Object>) startDateValue;
        @SuppressWarnings("unchecked")
        Comparable<Object> endDate = (Comparable<Object>) endDateValue;

        if (startDate.compareTo(endDate) >= 0) {
            HibernateConstraintValidatorContext hibernateContext = context.unwrap(
                HibernateConstraintValidatorContext.class);
            hibernateContext.disableDefaultConstraintViolation();
            hibernateContext.addMessageParameter("startDate", startDate.toString());
            hibernateContext.addMessageParameter("endDate", endDate.toString());
            hibernateContext.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                .addPropertyNode(endDateField)
                .addConstraintViolation();
            return false;
        }

        return true;
    }
}