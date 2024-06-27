package io.github.susimsek.springaisamples.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;

@RequiredArgsConstructor
public class HeaderValidator implements ConstraintValidator<ValidHeader, String> {

    private String headerName;
    private boolean notBlank;
    private int min;
    private int max;
    private String regexp;
    private String blankMessage;
    private String sizeMessage;
    private String patternMessage;

    @Override
    public void initialize(ValidHeader constraintAnnotation) {
        this.headerName = constraintAnnotation.headerName();
        this.notBlank = constraintAnnotation.notBlank();
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
        this.regexp = constraintAnnotation.regexp();
        this.blankMessage = constraintAnnotation.blankMessage();
        this.sizeMessage = constraintAnnotation.sizeMessage();
        this.patternMessage = constraintAnnotation.patternMessage();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        boolean isValid = true;
        context.disableDefaultConstraintViolation();

        if (notBlank && !StringUtils.hasText(value)) {
            addViolation(context, blankMessage);
            isValid = false;
        }
        if (value != null && (value.length() < min || value.length() > max)) {
            addViolation(context, sizeMessage);
            isValid = false;
        }
        if (StringUtils.hasText(regexp) && value != null && !value.matches(regexp)) {
            addViolation(context, patternMessage);
            isValid = false;
        }
        return isValid;
    }

    private void addViolation(ConstraintValidatorContext context, String message) {
        context.buildConstraintViolationWithTemplate(message)
            .addPropertyNode(headerName)
            .addConstraintViolation();
    }
}