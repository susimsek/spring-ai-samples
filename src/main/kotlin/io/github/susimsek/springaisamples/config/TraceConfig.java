package io.github.susimsek.springaisamples.config;

import io.github.susimsek.springaisamples.enums.FilterOrder;
import io.github.susimsek.springaisamples.trace.TraceArgumentResolver;
import io.github.susimsek.springaisamples.trace.TracingFilter;
import io.micrometer.tracing.Tracer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
public class TraceConfig {

    @Bean
    public TracingFilter traceFilter(
        RequestMatchersConfig requestMatchersConfig,
        Tracer tracer) {
        return TracingFilter.builder(tracer)
            .order(FilterOrder.TRACE.order())
            .requestMatchers(requestMatchersConfig.staticResources()).permitAll()
            .requestMatchers(requestMatchersConfig.swaggerPaths()).permitAll()
            .requestMatchers(requestMatchersConfig.actuatorPaths()).permitAll()
            .anyRequest().traced()
            .build();
    }

    @Bean
    public TraceArgumentResolver traceArgumentResolver() {
        return new TraceArgumentResolver();
    }
}