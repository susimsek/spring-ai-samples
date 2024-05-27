package io.github.susmisek.springaisamples.i18n;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

/**
 * Custom MessageSource implementation to support named parameters in messages.
 * This class extends {@link ResourceBundleMessageSource} to provide the ability
 * to replace placeholders in the form of {paramName} with their corresponding values
 * from a provided map.
 */
public class NamedParameterMessageSource extends ResourceBundleMessageSource
    implements ParameterMessageSource {

    private static final Pattern NAMED_PATTERN = Pattern.compile("\\{([a-zA-Z0-9]+)}");

    /**
     * Replaces named parameters in the message with their corresponding values.
     *
     * @param msg  the message containing named parameters
     * @param args the map containing parameter names and their values
     * @return the message with named parameters replaced by their values
     */
    private String replaceNamedParameters(String msg, Map<String, String> args) {
        Matcher matcher = NAMED_PATTERN.matcher(msg);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String paramName = matcher.group(1);
            String paramValue = Optional.ofNullable(args.get(paramName)).orElse("");
            matcher.appendReplacement(result, paramValue);
        }
        matcher.appendTail(result);
        return result.toString();
    }

    /**
     * Retrieves a message based on the code and the current locale.
     *
     * @param code the code of the message
     * @param args the arguments to be filled in the message (can be null)
     * @return the resolved message
     */
    @Override
    public String getMessage(String code, @Nullable Object... args) {
        return getMessageInternal(code, args, LocaleContextHolder.getLocale());
    }

    /**
     * Retrieves a message with named arguments based on the code and the current locale.
     *
     * @param code the code of the message
     * @param args the map containing parameter names and their values
     * @return the resolved message with named parameters replaced
     */
    @Override
    public String getMessageWithNamedArgs(String code, Map<String, String> args) {
        return getMessageWithNamedArgs(code, args, LocaleContextHolder.getLocale());
    }

    /**
     * Retrieves a message with named arguments based on the code and a specific locale.
     *
     * @param code   the code of the message
     * @param args   the map containing parameter names and their values
     * @param locale the locale to resolve the message for
     * @return the resolved message with named parameters replaced
     */
    @Override
    public String getMessageWithNamedArgs(String code, Map<String, String> args, Locale locale) {
        String message = super.getMessageInternal(code, null, locale);
        if (message != null && !CollectionUtils.isEmpty(args)) {
            message = replaceNamedParameters(message, args);
        }
        return message;
    }
}