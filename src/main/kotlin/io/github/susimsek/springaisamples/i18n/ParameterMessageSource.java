package io.github.susimsek.springaisamples.i18n;

import java.util.Locale;
import java.util.Map;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.lang.Nullable;

/**
 * A message source interface that supports named parameters.
 *
 * <p>
 * This interface extends the Spring Framework's {@link MessageSource} interface
 * to provide support for named parameters in messages. Classes implementing this
 * interface should override the {@link #getMessage(String, Object...)} method to
 * provide the functionality of resolving messages with named parameters.
 * </p>
 * Example usage:
 * <pre>
 * {@code
 * ParameterMessageSource messageSource = new NamedParameterMessageSource();
 * messageSource.setBasename("i18n/messages");
 * Map<String, Object> params = Map.of("name", "John");
 * String message = messageSource.getMessageWithNamedArgs("greeting.named", params);
 * }
 * </pre>
 */
public interface ParameterMessageSource extends MessageSource {

    /**
     * Retrieve a message from the message source, replacing named parameters in the message with provided values.
     *
     * @param code the code of the message to retrieve
     * @param args the arguments to be used for message retrieval, including named parameters
     * @return the resolved message with replaced named parameters
     * @throws NoSuchMessageException if the message with the specified code is not found
     */
    String getMessage(String code, @Nullable Object... args) throws NoSuchMessageException;

    /**
     * Retrieve a message from the message source, replacing named parameters in the message with provided values.
     *
     * @param code      the code of the message to retrieve
     * @param namedArgs the named arguments to be used for message retrieval
     * @return the resolved message with replaced named parameters
     * @throws NoSuchMessageException if the message with the specified code is not found
     */
    String getMessageWithNamedArgs(String code, @Nullable Map<String, Object> namedArgs) throws NoSuchMessageException;

    /**
     * Retrieve a message from the message source, replacing named parameters in the message with provided values.
     *
     * @param code      the code of the message to retrieve
     * @param namedArgs the named arguments to be used for message retrieval
     * @param locale    the locale for which to retrieve the message
     * @return the resolved message with replaced named parameters
     * @throws NoSuchMessageException if the message with the specified code is not found
     */
    String getMessageWithNamedArgs(String code, @Nullable Map<String, Object> namedArgs,
                                   Locale locale) throws NoSuchMessageException;

    /**
     * Retrieve messages starting with the given prefix.
     *
     * @param prefix the prefix of the messages to retrieve
     * @param locale the locale for which to retrieve the messages
     * @return a map of messages with keys starting with the given prefix
     */
    Map<String, String> getMessagesStartingWith(String prefix, Locale locale);
}