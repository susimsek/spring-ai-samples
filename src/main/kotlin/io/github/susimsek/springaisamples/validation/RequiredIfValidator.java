package io.github.susimsek.springaisamples.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.StringUtils;

public class RequiredIfValidator implements ConstraintValidator<RequiredIf, Object> {

    private String conditionField;
    private String conditionValue;
    private String requiredField;
    private String notNullMessage;
    private String notBlankMessage;

    @Override
    public void initialize(RequiredIf constraintAnnotation) {
        this.conditionField = constraintAnnotation.conditionField();
        this.conditionValue = constraintAnnotation.conditionValue();
        this.requiredField = constraintAnnotation.requiredField();
        this.notNullMessage = constraintAnnotation.notNullMessage();
        this.notBlankMessage = constraintAnnotation.notBlankMessage();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        BeanWrapperImpl beanWrapper = new BeanWrapperImpl(value);
        Object conditionFieldValue = beanWrapper.getPropertyValue(conditionField);
        Object requiredFieldValue = beanWrapper.getPropertyValue(requiredField);

        if (conditionFieldValue != null && conditionFieldValue.toString().equals(conditionValue)) {
            if (requiredFieldValue == null) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(notNullMessage)
                        .addPropertyNode(requiredField)
                        .addConstraintViolation();
                return false;
            }
            if (!StringUtils.hasText(requiredFieldValue.toString())) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(notBlankMessage)
                        .addPropertyNode(requiredField)
                        .addConstraintViolation();
                return false;
            }
        }
        return true;
    }
}