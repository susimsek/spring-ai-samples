package io.github.susimsek.springaisamples.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.regex.Pattern;

public class IPAddressValidator implements ConstraintValidator<IPAddress, String> {

    private static final Pattern IPV4_PATTERN =
        Pattern.compile("^((25[0-5]|2[0-4]\\d|1\\d\\d|\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|1\\d\\d|\\d\\d?)$");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        return IPV4_PATTERN.matcher(value).matches();
    }
}