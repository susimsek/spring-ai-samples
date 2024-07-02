package io.github.susimsek.springaisamples.config;

import io.github.susimsek.springaisamples.enums.FilterOrder;
import io.github.susimsek.springaisamples.exception.versioning.ApiVersionProblemSupport;
import io.github.susimsek.springaisamples.versioning.ApiVersion;
import io.github.susimsek.springaisamples.versioning.ApiVersionFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class VersioningConfig {

    @Bean
    public ApiVersionFilter apiVersionFilter(
        RequestMatchersConfig requestMatchersConfig,
        ApiVersionProblemSupport problemSupport) {
        return ApiVersionFilter.builder(problemSupport)
            .order(FilterOrder.API_VERSION.order())
            .requestMatchers(requestMatchersConfig.staticResources())
            .permitAll()
            .requestMatchers(requestMatchersConfig.swaggerPaths()).permitAll()
            .requestMatchers(requestMatchersConfig.actuatorPaths()).permitAll()
            .anyRequest()
            .supportedVersions(ApiVersion.V1.version())
            .versioned()
            .build();
    }
}