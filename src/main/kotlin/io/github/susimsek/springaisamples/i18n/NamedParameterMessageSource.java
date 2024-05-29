package io.github.susimsek.springaisamples.i18n;

import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.context.NoSuchMessageException;
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
public class NamedParameterMessageSource extends ResourceBundleMessageSource implements ParameterMessageSource {

    /**
     * Pattern to match named parameters in the format {paramName}.
     * This pattern matches sequences that conform to Java variable name constraints.
     */
    private static final Pattern NAMED_PATTERN = Pattern.compile("\\{([a-zA-Z_]\\w*)}");

    /**
     * Replaces named parameters in the message template with their corresponding values.
     *
     * @param messageTemplate the message containing named parameters
     * @param args            the map containing parameter names and their values
     * @return the message with named parameters replaced by their values
     */
    private String replaceNamedParameters(String messageTemplate, Map<String, String> args) {
        Matcher matcher = NAMED_PATTERN.matcher(messageTemplate);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String paramName = matcher.group(1);
            String paramValue = args.get(paramName);
            if (paramValue != null) {
                matcher.appendReplacement(result, paramValue);
            }
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
     * @throws NoSuchMessageException if the message with the specified code is not found
     */
    @Override
    public String getMessage(String code, @Nullable Object... args) throws NoSuchMessageException {
        return super.getMessage(code, args, LocaleContextHolder.getLocale());
    }

    /**
     * Retrieves a message with named arguments based on the code and the current locale.
     *
     * @param code the code of the message
     * @param args the map containing parameter names and their values
     * @return the resolved message with named parameters replaced
     * @throws NoSuchMessageException if the message with the specified code is not found
     */
    @Override
    public String getMessageWithNamedArgs(String code,
                                          @Nullable Map<String, String> args) throws NoSuchMessageException {
        return getMessageWithNamedArgs(code, args, LocaleContextHolder.getLocale());
    }

    /**
     * Retrieves a message with named arguments based on the code and a specific locale.
     *
     * @param code   the code of the message
     * @param args   the map containing parameter names and their values
     * @param locale the locale to resolve the message for
     * @return the resolved message with named parameters replaced
     * @throws NoSuchMessageException if the message with the specified code is not found
     */
    @Override
    public String getMessageWithNamedArgs(String code,
                                          @Nullable Map<String, String> args,
                                          Locale locale) throws NoSuchMessageException {
        String message = super.getMessage(code, null, locale);
        if (!CollectionUtils.isEmpty(args)) {
            message = replaceNamedParameters(message, args);
        }
        return message;
    }
}