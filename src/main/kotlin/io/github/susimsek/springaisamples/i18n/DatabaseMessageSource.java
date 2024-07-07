package io.github.susimsek.springaisamples.i18n;

import io.github.susimsek.springaisamples.service.MessageService;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

/**
 * A custom message source that retrieves messages from a database.
 * This class extends {@link AbstractNamedParameterMessageSource} and uses a
 * {@link MessageService} to fetch messages based on locale.
 */
public class DatabaseMessageSource extends AbstractNamedParameterMessageSource {

    private final MessageSourceControl control;
    private long cacheMillis = -1L;

    /**
     * Constructs a new DatabaseMessageSource.
     *
     * @param messageService the service to fetch messages from the database
     */
    public DatabaseMessageSource(MessageService messageService) {
        this.setDefaultEncoding("UTF-8");
        this.control = new MessageSourceControl(messageService);
    }

    /**
     * Sets the cache duration in milliseconds.
     *
     * @param cacheMillis the cache duration in milliseconds
     */
    @Override
    public void setCacheMillis(long cacheMillis) {
        this.cacheMillis = cacheMillis;
    }

    /**
     * Gets the cache duration in milliseconds.
     *
     * @return the cache duration in milliseconds
     */
    @Override
    protected long getCacheMillis() {
        return this.cacheMillis;
    }

    /**
     * Retrieves a resource bundle for the given base name and locale.
     *
     * @param basename the base name of the resource bundle
     * @param locale   the locale for which the resource bundle should be retrieved
     * @return the resource bundle
     * @throws MissingResourceException if no resource bundle for the specified base name can be found
     */
    @NonNull
    @Override
    protected ResourceBundle doGetBundle(@NonNull String basename,
                                         @NonNull Locale locale) throws MissingResourceException {
        ClassLoader classLoader = this.getBundleClassLoader();
        Assert.state(classLoader != null, "No bundle ClassLoader set");
        return ResourceBundle.getBundle(basename, locale, classLoader, control);
    }

    /**
     * A custom resource bundle that retrieves messages from a map.
     */
    private static class DatabaseResourceBundle extends ResourceBundle {

        private final Map<String, String> messages;

        /**
         * Constructs a new DatabaseResourceBundle.
         *
         * @param messages the map of messages
         */
        public DatabaseResourceBundle(Map<String, String> messages) {
            this.messages = messages;
        }

        /**
         * Retrieves the message for the given key.
         *
         * @param key the key for the desired message
         * @return the message for the given key
         */
        @Override
        protected Object handleGetObject(@NonNull String key) {
            return messages.get(key);
        }

        /**
         * Gets the keys for all messages.
         *
         * @return an enumeration of all message keys
         */
        @NonNull
        @Override
        public Enumeration<String> getKeys() {
            return Collections.enumeration(messages.keySet());
        }
    }

    /**
     * A custom control class for managing resource bundles.
     */
    private class MessageSourceControl extends ResourceBundle.Control {
        private final MessageService messageService;
        private final Map<Locale, Long> lastUpdateCache = new ConcurrentHashMap<>();

        /**
         * Constructs a new MessageSourceControl.
         *
         * @param messageService the service to fetch messages from the database
         */
        public MessageSourceControl(MessageService messageService) {
            this.messageService = messageService;
        }

        /**
         * Creates a new resource bundle for the given base name and locale.
         *
         * @param baseName the base name of the resource bundle
         * @param locale   the locale for which the resource bundle should be created
         * @param format   the format of the resource bundle
         * @param loader   the class loader to use
         * @param reload   whether to reload the resource bundle
         * @return the resource bundle
         */
        @Override
        public ResourceBundle newBundle(String baseName,
                                        Locale locale,
                                        String format,
                                        ClassLoader loader,
                                        boolean reload) {
            Map<String, String> messages = messageService.getMessages(locale.getLanguage());
            return new DatabaseResourceBundle(messages);
        }

        /**
         * Gets the time-to-live for the resource bundle cache.
         *
         * @param baseName the base name of the resource bundle
         * @param locale   the locale for which the resource bundle should be cached
         * @return the time-to-live in milliseconds
         */
        @Override
        public long getTimeToLive(String baseName, Locale locale) {
            return getCacheMillis() >= 0L ? getCacheMillis() : super.getTimeToLive(baseName, locale);
        }

        /**
         * Gets the fallback locale for the given base name and locale.
         *
         * @param baseName the base name of the resource bundle
         * @param locale   the locale for which the fallback should be found
         * @return the fallback locale
         */
        @Override
        public Locale getFallbackLocale(String baseName, Locale locale) {
            Locale defaultLocale = getDefaultLocale();
            return defaultLocale != null && !defaultLocale.equals(locale) ? defaultLocale : null;
        }

        /**
         * Determines whether the resource bundle needs to be reloaded.
         *
         * @param baseName the base name of the resource bundle
         * @param locale   the locale for which the resource bundle should be checked
         * @param format   the format of the resource bundle
         * @param loader   the class loader to use
         * @param bundle   the resource bundle
         * @param loadTime the load time of the resource bundle
         * @return true if the resource bundle needs to be reloaded, false otherwise
         */
        @Override
        public boolean needsReload(String baseName, Locale locale, String format, ClassLoader loader,
                                   ResourceBundle bundle, long loadTime) {
            Long lastUpdateTime = lastUpdateCache.get(locale);
            long currentTime = System.currentTimeMillis();

            if (lastUpdateTime == null || currentTime - lastUpdateTime > getTimeToLive(baseName, locale)) {
                lastUpdateCache.put(locale, currentTime);
                return true;
            }

            return false;
        }
    }
}