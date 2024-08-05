package io.github.susimsek.springaisamples.utils;


import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtils {

    public String toSnakeCase(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }
}
