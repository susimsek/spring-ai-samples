package io.github.susmisek.springaisamples.i18n;

import java.util.Map;
import org.springframework.context.MessageSource;
import org.springframework.lang.Nullable;

/**
 * A message source interface that supports named parameters.
 *
 * <p>
 * This interface extends the Spring Framework's {@link org.springframework.context.MessageSource} interface
 * to provide support for named parameters in messages. Classes implementing this interface should override
 * the {@link #getMessage(String, Object...)}
 * method to provide the functionality of resolving messages with named parameters.
 * </p>
 */
public interface ParameterMessageSource extends MessageSource {

    /**
     * Retrieve a message from the message source, replacing named parameters in the message with provided values.
     *
     * @param code the code of the message to retrieve
     * @param args the arguments to be used for message retrieval, including named parameters
     * @return the resolved message with replaced named parameters
     */
    String getMessage(String code, @Nullable Object... args);

    /**
     * Retrieve a message from the message source, replacing named parameters in the message with provided values.
     *
     * @param code the code of the message to retrieve
     * @param namedArgs the named arguments to be used for message retrieval
     * @return the resolved message with replaced named parameters
     */
    String getMessageWithNamedArgs(String code, @Nullable Map<String, String> namedArgs);

    /**
     * Retrieve a message from the message source, replacing named parameters in the message with provided values.
     *
     * @param code the code of the message to retrieve
     * @param namedArgs the named arguments to be used for message retrieval
     * @param locale the locale for which to retrieve the message
     * @return the resolved message with replaced named parameters
     */
    String getMessageWithNamedArgs(String code, @Nullable Map<String, String> namedArgs, java.util.Locale locale);
}
