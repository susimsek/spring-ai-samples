package io.github.susimsek.springaisamples.i18n;

import io.github.susimsek.springaisamples.service.MessageService;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

public class DatabaseMessageSource extends ResourceBundleMessageSource {

    private final MessageSourceControl control;
    private long cacheMillis = -1L;


    public DatabaseMessageSource(MessageService messageService) {
        this.setDefaultEncoding("UTF-8");
        this.control = new MessageSourceControl(messageService);
    }

    @Override
    public void setCacheMillis(long cacheMillis) {
        this.cacheMillis = cacheMillis;
    }

    @Override
    protected long getCacheMillis() {
        return this.cacheMillis;
    }

    @NonNull
    @Override
    protected ResourceBundle doGetBundle(@NonNull String basename,
                                         @NonNull Locale locale) throws MissingResourceException {
        ClassLoader classLoader = this.getBundleClassLoader();
        Assert.state(classLoader != null, "No bundle ClassLoader set");
        return ResourceBundle.getBundle(basename, locale, classLoader, control);
    }

    private static class DatabaseResourceBundle extends ResourceBundle {

        private final Map<String, String> messages;

        public DatabaseResourceBundle(Map<String, String> messages) {
            this.messages = messages;
        }

        @Override
        protected Object handleGetObject(@NonNull String key) {
            return messages.get(key);
        }

        @NonNull
        @Override
        public Enumeration<String> getKeys() {
            return Collections.enumeration(messages.keySet());
        }
    }

    private class MessageSourceControl extends ResourceBundle.Control {
        private final MessageService messageService;
        private final Map<Locale, Long> lastUpdateCache = new ConcurrentHashMap<>();

        public MessageSourceControl(MessageService messageService) {
            this.messageService = messageService;
        }

        @Override
        public ResourceBundle newBundle(String baseName,
                                        Locale locale,
                                        String format,
                                        ClassLoader loader,
                                        boolean reload) {
            Map<String, String> messages = messageService.getMessages(locale.getLanguage());
            return new DatabaseResourceBundle(messages);
        }

        @Override
        public long getTimeToLive(String baseName, Locale locale) {
            return getCacheMillis() >= 0L ? getCacheMillis() : super.getTimeToLive(baseName, locale);
        }

        @Override
        public Locale getFallbackLocale(String baseName, Locale locale) {
            Locale defaultLocale = getDefaultLocale();
            return defaultLocale != null && !defaultLocale.equals(locale) ? defaultLocale : null;
        }

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