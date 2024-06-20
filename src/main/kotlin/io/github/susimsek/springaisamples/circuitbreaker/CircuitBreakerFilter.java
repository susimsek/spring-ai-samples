package io.github.susimsek.springaisamples.circuitbreaker;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.susimsek.springaisamples.enums.FilterOrder;
import io.github.susimsek.springaisamples.exception.circuitbreaker.CircuitBreakerException;
import io.github.susimsek.springaisamples.exception.circuitbreaker.CircuitBreakerExceptionHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.security.config.annotation.web.AbstractRequestMatcherRegistry;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class CircuitBreakerFilter extends OncePerRequestFilter implements Ordered {

    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final CircuitBreakerExceptionHandler circuitBreakerExceptionHandler;
    private final List<RequestMatcherConfig> requestMatcherConfigs;
    private final boolean defaultProtectedByCircuitBreaker;
    private final int order;

    private static final String DEFAULT_CIRCUIT_BREAKER_NAME = "default";
    private static final String CIRCUIT_BREAKER_OPEN_MESSAGE = "Circuit breaker open";

    private String currentCircuitBreakerName = DEFAULT_CIRCUIT_BREAKER_NAME;

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        return requestMatcherConfigs.isEmpty() || requestMatcherConfigs.stream()
            .filter(config -> config.requestMatcher.matches(request))
            .findFirst()
            .map(config -> {
                currentCircuitBreakerName = config.circuitBreakerName;
                return !config.protectedByCircuitBreaker;
            })
            .orElse(!defaultProtectedByCircuitBreaker);
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
        throws ServletException, IOException {
        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(currentCircuitBreakerName);
        long startTime = System.nanoTime();
        if (circuitBreaker.tryAcquirePermission()) {
            try {
                filterChain.doFilter(request, response);
                long duration = System.nanoTime() - startTime;
                circuitBreaker.onSuccess(duration, TimeUnit.NANOSECONDS);
            } catch (Exception e) {
                long duration = System.nanoTime() - startTime;
                circuitBreaker.onError(duration, TimeUnit.NANOSECONDS, e);
                throw e;
            }
        } else {
            handleCircuitBreakerException(request, response);
        }
    }

    private void handleCircuitBreakerException(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
        CircuitBreakerException exception = new CircuitBreakerException(
            currentCircuitBreakerName, CIRCUIT_BREAKER_OPEN_MESSAGE);
        circuitBreakerExceptionHandler.handle(request, response, exception);
    }

    @AllArgsConstructor
    private static class RequestMatcherConfig {
        private final RequestMatcher requestMatcher;
        private String circuitBreakerName;
        private boolean protectedByCircuitBreaker;
    }

    public interface InitialBuilder {
        InitialBuilder order(int order);

        AfterRequestMatchersBuilder anyRequest();

        AfterRequestMatchersBuilder requestMatchers(HttpMethod method, String... patterns);

        AfterRequestMatchersBuilder requestMatchers(String... patterns);

        AfterRequestMatchersBuilder requestMatchers(RequestMatcher... requestMatchers);

        CircuitBreakerFilter build();
    }

    public interface AfterRequestMatchersBuilder {
        InitialBuilder permitAll();

        InitialBuilder protectedByCircuitBreaker();

        CircuitBreakerConfigBuilder circuitBreakerName(String circuitBreakerName);
    }

    public interface CircuitBreakerConfigBuilder {
        InitialBuilder protectedByCircuitBreaker();
    }

    public static InitialBuilder builder(CircuitBreakerRegistry circuitBreakerRegistry, CircuitBreakerExceptionHandler circuitBreakerExceptionHandler) {
        return new Builder(circuitBreakerRegistry, circuitBreakerExceptionHandler);
    }

    private static class Builder extends AbstractRequestMatcherRegistry<Builder>
        implements InitialBuilder, AfterRequestMatchersBuilder, CircuitBreakerConfigBuilder {

        private final CircuitBreakerRegistry circuitBreakerRegistry;
        private final CircuitBreakerExceptionHandler circuitBreakerExceptionHandler;
        private final List<RequestMatcherConfig> requestMatcherConfigs = new ArrayList<>();
        private boolean anyRequestConfigured = false;
        private boolean defaultProtectedByCircuitBreaker = true;
        private int order = FilterOrder.CIRCUIT_BREAKER.order();
        private int lastIndex = 0;

        private Builder(CircuitBreakerRegistry circuitBreakerRegistry, CircuitBreakerExceptionHandler circuitBreakerExceptionHandler) {
            this.circuitBreakerRegistry = circuitBreakerRegistry;
            this.circuitBreakerExceptionHandler = circuitBreakerExceptionHandler;
        }

        @Override
        public Builder requestMatchers(HttpMethod method, String... patterns) {
            lastIndex = requestMatcherConfigs.size();
            for (String pattern : patterns) {
                this.requestMatcherConfigs.add(new RequestMatcherConfig(
                    new AntPathRequestMatcher(pattern, method.name()), DEFAULT_CIRCUIT_BREAKER_NAME, true));
            }
            return this;
        }

        @Override
        public Builder requestMatchers(String... patterns) {
            lastIndex = requestMatcherConfigs.size();
            for (String pattern : patterns) {
                this.requestMatcherConfigs.add(new RequestMatcherConfig(
                    new AntPathRequestMatcher(pattern), DEFAULT_CIRCUIT_BREAKER_NAME, true));
            }
            return this;
        }

        @Override
        public Builder requestMatchers(RequestMatcher... requestMatchers) {
            lastIndex = requestMatcherConfigs.size();
            for (RequestMatcher requestMatcher : requestMatchers) {
                this.requestMatcherConfigs.add(new RequestMatcherConfig(
                    requestMatcher, DEFAULT_CIRCUIT_BREAKER_NAME, true));
            }
            return this;
        }

        @Override
        public Builder anyRequest() {
            Assert.state(!this.anyRequestConfigured, "anyRequest() can only be called once");
            this.anyRequestConfigured = true;
            return this;
        }

        @Override
        public Builder permitAll() {
            Assert.state(anyRequestConfigured || !requestMatcherConfigs.isEmpty(),
                "permitAll() can only be called after requestMatchers() or anyRequest()");
            if (anyRequestConfigured) {
                this.defaultProtectedByCircuitBreaker = false;
            } else {
                requestMatcherConfigs.stream()
                    .skip(lastIndex)
                    .forEach(config -> config.protectedByCircuitBreaker = false);
            }
            return this;
        }

        @Override
        public Builder circuitBreakerName(String circuitBreakerName) {
            Assert.state(anyRequestConfigured || !requestMatcherConfigs.isEmpty(),
                "circuitBreakerName() can only be called after requestMatchers() or anyRequest()");
            requestMatcherConfigs.stream()
                .skip(lastIndex)
                .forEach(config -> config.circuitBreakerName = circuitBreakerName);
            return this;
        }

        @Override
        public Builder protectedByCircuitBreaker() {
            Assert.state(anyRequestConfigured || !requestMatcherConfigs.isEmpty(),
                "protectedByCircuitBreaker() can only be called after requestMatchers() or anyRequest())");
            if (anyRequestConfigured) {
                this.defaultProtectedByCircuitBreaker = true;
            } else {
                requestMatcherConfigs.stream()
                    .skip(lastIndex)
                    .forEach(config -> config.protectedByCircuitBreaker = true);
            }
            return this;
        }

        @Override
        public Builder order(int order) {
            this.order = order;
            return this;
        }

        @Override
        public CircuitBreakerFilter build() {
            return new CircuitBreakerFilter(circuitBreakerRegistry,
                circuitBreakerExceptionHandler,
                requestMatcherConfigs, defaultProtectedByCircuitBreaker, order);
        }

        @Override
        protected Builder chainRequestMatchers(List<RequestMatcher> requestMatchers) {
            this.requestMatchers(requestMatchers.toArray(new RequestMatcher[0]));
            return this;
        }
    }
}