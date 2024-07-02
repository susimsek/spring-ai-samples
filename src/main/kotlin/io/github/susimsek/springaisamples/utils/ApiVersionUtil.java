package io.github.susimsek.springaisamples.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ApiVersionUtil {

    private static final Pattern VERSION_PATTERN = Pattern.compile("/api/(v\\d+)/");

    public static String getVersionFromUri(String path) {
        Matcher matcher = VERSION_PATTERN.matcher(path);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "v1";
    }
}