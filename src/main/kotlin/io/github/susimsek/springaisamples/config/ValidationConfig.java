package io.github.susimsek.springaisamples.config;

import static io.github.susimsek.springaisamples.idempotency.IdempotencyConstants.IDEMPOTENCY_PATTERN_REGEX;
import static io.github.susimsek.springaisamples.trace.TraceConstants.CORRELATION_ID_PATTERN_REGEX;
import static io.github.susimsek.springaisamples.trace.TraceConstants.REQUEST_ID_PATTERN_REGEX;

import io.github.susimsek.springaisamples.enums.FilterOrder;
import io.github.susimsek.springaisamples.exception.header.HeaderValidationProblemSupport;
import io.github.susimsek.springaisamples.i18n.ParameterMessageSource;
import io.github.susimsek.springaisamples.idempotency.IdempotencyConstants;
import io.github.susimsek.springaisamples.trace.TraceConstants;
import io.github.susimsek.springaisamples.validation.HeaderValidationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class ValidationConfig {

    @Bean
    public HeaderValidationFilter headerValidationFilter(
        RequestMatchersConfig requestMatchersConfig,
        HeaderValidationProblemSupport problemSupport,
        ParameterMessageSource messageSource) {
        return HeaderValidationFilter.builder(messageSource, problemSupport)
            .order(FilterOrder.HEADER_VALIDATION.order())
            .requestMatchers(requestMatchersConfig.staticResources())
            .permitAll()
            .requestMatchers(requestMatchersConfig.swaggerPaths()).permitAll()
            .requestMatchers(requestMatchersConfig.actuatorPaths()).permitAll()
            .requestMatchers(requestMatchersConfig.signPath())
            .headerName(IdempotencyConstants.IDEMPOTENCY_HEADER_NAME)
            .notBlank().min(8).max(36).regexp(IDEMPOTENCY_PATTERN_REGEX)
            .validated()
            .anyRequest()
            .headerName(TraceConstants.REQUEST_ID_HEADER_NAME)
            .notBlank().min(8).max(36).regexp(REQUEST_ID_PATTERN_REGEX)
            .headerName(TraceConstants.CORRELATION_ID_HEADER_NAME)
            .notBlank().min(8).max(36).regexp(CORRELATION_ID_PATTERN_REGEX)
            .validated()
            .build();
    }
}