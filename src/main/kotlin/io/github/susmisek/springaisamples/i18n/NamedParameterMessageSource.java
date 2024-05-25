package io.github.susmisek.springaisamples.i18n;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
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

    @Getter
    private final Map<String, String> namedParameters = new ConcurrentHashMap<>();

    private static final Pattern NAMED_PATTERN = Pattern.compile("\\{([a-zA-Z0-9]+)}");

    private boolean hasNamedParameters(String msg) {
        return NAMED_PATTERN.matcher(msg).find();
    }

    private String replaceNamedParameters(String msg) {
        Matcher matcher = NAMED_PATTERN.matcher(msg);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String paramName = matcher.group(1);
            String paramValue = Optional.ofNullable(namedParameters.get(paramName)).orElse("");
            matcher.appendReplacement(result, paramValue);
        }
        matcher.appendTail(result);
        return result.toString();
    }

    public void addNamedParameter(@NonNull String paramName, @NonNull String paramValue) {
        namedParameters.put(paramName, paramValue);
    }

    public void setNamedParameters(@NonNull Map<String, String> namedParameters) {
        this.namedParameters.clear();
        this.namedParameters.putAll(namedParameters);
    }

    @Override
    protected String getMessageInternal(String code, Object[] args, Locale locale) {
        String message = super.getMessageInternal(code, args, locale);
        if (message != null && !CollectionUtils.isEmpty(namedParameters) && hasNamedParameters(message)) {
            message = replaceNamedParameters(message);
        }
        return message;
    }

    @Override
    public String getMessage(String code, @Nullable Object... args) {
        return getMessageInternal(code, args, LocaleContextHolder.getLocale());
    }

    @Override
    public String getMessageWithNamedArgs(String code, Map<String, String> args) {
        return getMessageWithNamedArgs(code, args, LocaleContextHolder.getLocale());
    }

    @Override
    public String getMessageWithNamedArgs(String code, Map<String, String> args, Locale locale) {
        if (!CollectionUtils.isEmpty(args)) {
            this.setNamedParameters(args);
        }
        return getMessageInternal(code, null, locale);
    }
}