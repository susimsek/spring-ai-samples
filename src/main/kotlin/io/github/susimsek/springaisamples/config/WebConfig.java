package io.github.susimsek.springaisamples.config;

import io.github.susimsek.springaisamples.trace.TraceArgumentResolver;
import io.github.susimsek.springaisamples.versioning.ApiInfoArgumentResolver;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration(proxyBeanMethods = false)
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final TraceArgumentResolver traceArgumentResolver;
    private final ApiInfoArgumentResolver apiInfoArgumentResolver;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/api-docs/**")
            .addResourceLocations("classpath:/static/api-docs/");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addRedirectViewController("/api-docs", "/api-docs/index.html");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(traceArgumentResolver);
        resolvers.add(apiInfoArgumentResolver);
    }
}