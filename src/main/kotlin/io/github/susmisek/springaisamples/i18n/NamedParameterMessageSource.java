package io.github.susmisek.springaisamples.i18n;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

/**
 * A message source class that supports named parameters.
 *
 * <p>
 * This class extends the Spring Framework's {@link org.springframework.context.MessageSource} interface,
 * allowing the use of named parameters in messages. Named parameters can be included in message strings
 * using the format {paramName}, and their values can be dynamically replaced when retrieving messages.
 * </p>
 */
public class NamedParameterMessageSource extends ResourceBundleMessageSource implements ParameterMessageSource {

    // Map to store named parameters
    @Getter
    private final Map<String, String> namedParameters = new ConcurrentHashMap<>();

    // Regular expression pattern to match named parameters in message strings
    private static final Pattern NAMED_PATTERN = Pattern.compile("\\{([a-zA-Z0-9]+)}");

    /**
     * Checks if the given message contains named parameters.
     *
     * @param msg the message to check
     * @return {@code true} if named parameters are found, {@code false} otherwise
     */
    private boolean hasNamedParameters(String msg) {
        return NAMED_PATTERN.matcher(msg).find();
    }

    /**
     * Replaces named parameters in the given message with their values and returns the new message.
     *
     * @param msg the message to process
     * @return the processed message with replaced named parameters
     */
    private String replaceNamedParameters(String msg) {
        Matcher matcher = NAMED_PATTERN.matcher(msg);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String paramName = matcher.group(1);
            String paramValue = namedParameters.getOrDefault(paramName, "");
            matcher.appendReplacement(result, paramValue);
        }
        matcher.appendTail(result);
        return result.toString();
    }

    /**
     * Adds a named parameter.
     *
     * @param paramName  the name of the parameter to add
     * @param paramValue the value of the parameter to add
     */
    public void addNamedParameter(@NonNull String paramName, @NonNull String paramValue) {
        namedParameters.put(paramName, paramValue);
    }

    /**
     * Sets all named parameters.
     *
     * @param namedParameters the named parameters to set
     */
    public void setNamedParameters(@NonNull Map<String, String> namedParameters) {
        this.namedParameters.clear();
        this.namedParameters.putAll(namedParameters);
    }

    /**
     * Retrieves a message with the given code and replaces named parameters in the message before returning it.
     *
     * @param code the code of the message to retrieve
     * @param args the arguments to be used for the message retrieval
     * @return the processed message with replaced named parameters
     */
    @Override
    protected String getMessageInternal(String code, Object[] args, Locale locale) {
        String message = super.getMessageInternal(code, args, locale);
        if (message != null && !CollectionUtils.isEmpty(namedParameters) && hasNamedParameters(message)) {
            message = replaceNamedParameters(message);
        }
        return message;
    }

    /**
     * Retrieves a message with the given code and replaces named parameters in the message before returning it.
     *
     * @param code the code of the message to retrieve
     * @param args the arguments to be used for the message retrieval
     * @return the processed message with replaced named parameters
     */
    @Override
    public String getMessage(String code, @Nullable Object... args) {
        return getMessageInternal(code, args, LocaleContextHolder.getLocale());
    }

    /**
     * Retrieves a message with the given code and replaces named parameters in the message before returning it.
     *
     * @param code the code of the message to retrieve
     * @param args the arguments to be used for the message retrieval
     * @return the processed message with replaced named parameters
     */
    @Override
    public String getMessageWithNamedArgs(String code, Map<String, String> args) {
        return getMessageWithNamedArgs(code, args, LocaleContextHolder.getLocale());
    }

    /**
     * Retrieves a message with the given code and replaces named parameters in the message before returning it.
     *
     * @param code   the code of the message to retrieve
     * @param args   the arguments to be used for the message retrieval
     * @param locale the locale of the message
     * @return the processed message with replaced named parameters
     */
    @Override
    public String getMessageWithNamedArgs(String code, Map<String, String> args, Locale locale) {
        if (!CollectionUtils.isEmpty(args)) {
            this.setNamedParameters(args);
        }
        return getMessageInternal(code, null, locale);
    }
}
