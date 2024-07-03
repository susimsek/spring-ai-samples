package io.github.susimsek.springaisamples.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TCKNValidator implements ConstraintValidator<TCKN, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Null values are considered valid. Use @NotNull for non-null validation.
        }

        if (value.length() != 11 || !value.matches("\\d+")) {
            return false;
        }

        if (value.charAt(0) == '0') {
            return false;
        }

        int[] digits = new int[11];
        for (int i = 0; i < 11; i++) {
            digits[i] = Character.getNumericValue(value.charAt(i));
        }

        int sumOdd = digits[0] + digits[2] + digits[4] + digits[6] + digits[8];
        int sumEven = digits[1] + digits[3] + digits[5] + digits[7];
        int checkSum = (sumOdd * 7 - sumEven) % 10;

        if (checkSum != digits[9]) {
            return false;
        }

        int sumAll = sumOdd + sumEven + digits[9];
        return sumAll % 10 == digits[10];
    }
}