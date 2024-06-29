package io.github.susimsek.springaisamples.config;

import io.github.susimsek.springaisamples.i18n.NamedParameterMessageSource;
import io.github.susimsek.springaisamples.i18n.ParameterMessageSource;
import jakarta.validation.MessageInterpolator;
import jakarta.validation.Validator;
import java.time.Duration;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.springframework.boot.autoconfigure.context.MessageSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.context.MessageSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.util.StringUtils;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;


@Configuration(proxyBeanMethods = false)
@Import(MessageSourceAutoConfiguration.class)
public class LocaleConfig {

    @Bean
    public MessageInterpolator messageInterpolator(Validator validator) {
        if (validator instanceof LocalValidatorFactoryBean localValidatorFactoryBean) {
            return localValidatorFactoryBean.getMessageInterpolator();
        }
        return new ResourceBundleMessageInterpolator();
    }

    @Bean
    public ParameterMessageSource messageSource(MessageSourceProperties properties) {
        NamedParameterMessageSource messageSource = new NamedParameterMessageSource();
        if (StringUtils.hasText(properties.getBasename())) {
            messageSource.setBasenames(StringUtils
                .commaDelimitedListToStringArray(StringUtils.trimAllWhitespace(properties.getBasename())));
        }
        if (properties.getEncoding() != null) {
            messageSource.setDefaultEncoding(properties.getEncoding().name());
        }
        messageSource.setFallbackToSystemLocale(properties.isFallbackToSystemLocale());
        Duration cacheDuration = properties.getCacheDuration();
        if (cacheDuration != null) {
            messageSource.setCacheMillis(cacheDuration.toMillis());
        }
        messageSource.setAlwaysUseMessageFormat(properties.isAlwaysUseMessageFormat());
        messageSource.setUseCodeAsDefaultMessage(properties.isUseCodeAsDefaultMessage());
        return messageSource;
    }
}
