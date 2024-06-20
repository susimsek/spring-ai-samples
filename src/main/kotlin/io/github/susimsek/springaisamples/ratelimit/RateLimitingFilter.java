package io.github.susimsek.springaisamples.ratelimit;

import static io.github.susimsek.springaisamples.ratelimit.RateLimitConstants.RATE_LIMIT_LIMIT_HEADER_NAME;
import static io.github.susimsek.springaisamples.ratelimit.RateLimitConstants.RATE_LIMIT_REMAINING_HEADER_NAME;
import static io.github.susimsek.springaisamples.ratelimit.RateLimitConstants.RATE_LIMIT_RESET_HEADER_NAME;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.susimsek.springaisamples.enums.FilterOrder;
import io.github.susimsek.springaisamples.exception.ratelimit.RateLimitExceededException;
import io.github.susimsek.springaisamples.exception.ratelimit.RateLimitExceptionHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.security.config.annotation.web.AbstractRequestMatcherRegistry;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class RateLimitingFilter extends OncePerRequestFilter implements Ordered {

    private final RateLimiterRegistry rateLimiterRegistry;
    private final RateLimitExceptionHandler rateLimitExceptionHandler;
    private final List<RequestMatcherConfig> requestMatcherConfigs;
    private final boolean defaultRateLimited;
    private final int order;

    private static final String DEFAULT_RATE_LIMITER_NAME = "default";
    private static final String TOO_MANY_REQUESTS_MESSAGE = "Too many requests";

    private String currentRateLimiterName = DEFAULT_RATE_LIMITER_NAME;

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        return requestMatcherConfigs.stream()
            .filter(config -> config.requestMatcher.matches(request))
            .findFirst()
            .map(config -> {
                currentRateLimiterName = config.rateLimiterName;
                return !config.rateLimited;
            })
            .orElse(!defaultRateLimited);
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
        throws ServletException, IOException {
        RateLimiter rateLimiter = rateLimiterRegistry.rateLimiter(currentRateLimiterName);
        RateLimiter.Metrics metrics = rateLimiter.getMetrics();
        long availablePermissions = metrics.getAvailablePermissions();
        Duration timeUntilReset = rateLimiter.getRateLimiterConfig().getLimitRefreshPeriod();
        int limitForPeriod = rateLimiter.getRateLimiterConfig().getLimitForPeriod();
        Instant nextReset = Instant.now().plus(timeUntilReset);

        response.setHeader(HttpHeaders.RETRY_AFTER, String.valueOf(nextReset.getEpochSecond()));
        response.setHeader(RATE_LIMIT_LIMIT_HEADER_NAME, String.valueOf(limitForPeriod));
        response.setHeader(RATE_LIMIT_REMAINING_HEADER_NAME, String.valueOf(availablePermissions));
        response.setHeader(RATE_LIMIT_RESET_HEADER_NAME, String.valueOf(nextReset.getEpochSecond()));

        if (rateLimiter.acquirePermission()) {
            filterChain.doFilter(request, response);
        } else {
            handleRateLimitingException(request, response, limitForPeriod,
                availablePermissions, nextReset.getEpochSecond());
        }
    }

    private void handleRateLimitingException(HttpServletRequest request,
                                             HttpServletResponse response,
                                             int limitForPeriod,
                                             long availablePermissions,
                                             long resetTime) throws IOException, ServletException {
        RateLimitExceededException exception = new RateLimitExceededException(
            currentRateLimiterName, TOO_MANY_REQUESTS_MESSAGE,
            limitForPeriod, availablePermissions, resetTime);
        rateLimitExceptionHandler.handle(request, response, exception);
    }

    @AllArgsConstructor
    private static class RequestMatcherConfig {
        private final RequestMatcher requestMatcher;
        private String rateLimiterName;
        private boolean rateLimited;
    }

    public interface InitialBuilder {
        InitialBuilder order(int order);

        AfterRequestMatchersBuilder anyRequest();

        AfterRequestMatchersBuilder requestMatchers(HttpMethod method, String... patterns);

        AfterRequestMatchersBuilder requestMatchers(String... patterns);

        AfterRequestMatchersBuilder requestMatchers(RequestMatcher... requestMatchers);

        RateLimitingFilter build();
    }

    public interface AfterRequestMatchersBuilder {
        InitialBuilder permitAll();

        InitialBuilder rateLimited();

        RateLimitConfigBuilder rateLimiterName(String rateLimiterName);
    }

    public interface RateLimitConfigBuilder {
        InitialBuilder rateLimited();
    }

    public static InitialBuilder builder(RateLimiterRegistry rateLimiterRegistry,
                                         RateLimitExceptionHandler rateLimitExceptionHandler) {
        return new Builder(rateLimiterRegistry, rateLimitExceptionHandler);
    }

    private static class Builder extends AbstractRequestMatcherRegistry<Builder>
        implements InitialBuilder, AfterRequestMatchersBuilder, RateLimitConfigBuilder {

        private final RateLimiterRegistry rateLimiterRegistry;
        private final RateLimitExceptionHandler rateLimitExceptionHandler;
        private final List<RequestMatcherConfig> requestMatcherConfigs = new ArrayList<>();
        private boolean anyRequestConfigured = false;
        private boolean defaultRateLimited = true;
        private int order = FilterOrder.RATE_LIMIT.order();
        private int lastIndex = 0;

        private Builder(RateLimiterRegistry rateLimiterRegistry,
                        RateLimitExceptionHandler rateLimitExceptionHandler) {
            this.rateLimiterRegistry = rateLimiterRegistry;
            this.rateLimitExceptionHandler = rateLimitExceptionHandler;
        }

        @Override
        public Builder requestMatchers(HttpMethod method, String... patterns) {
            lastIndex = requestMatcherConfigs.size();
            for (String pattern : patterns) {
                this.requestMatcherConfigs.add(new RequestMatcherConfig(
                    new AntPathRequestMatcher(pattern, method.name()), DEFAULT_RATE_LIMITER_NAME, true));
            }
            return this;
        }

        @Override
        public Builder requestMatchers(String... patterns) {
            lastIndex = requestMatcherConfigs.size();
            for (String pattern : patterns) {
                this.requestMatcherConfigs.add(new RequestMatcherConfig(
                    new AntPathRequestMatcher(pattern), DEFAULT_RATE_LIMITER_NAME, true));
            }
            return this;
        }

        @Override
        public Builder requestMatchers(RequestMatcher... requestMatchers) {
            lastIndex = requestMatcherConfigs.size();
            for (RequestMatcher requestMatcher : requestMatchers) {
                this.requestMatcherConfigs.add(new RequestMatcherConfig(
                    requestMatcher, DEFAULT_RATE_LIMITER_NAME, true));
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
                this.defaultRateLimited = false;
            } else {
                requestMatcherConfigs.stream()
                    .skip(lastIndex)
                    .forEach(config -> config.rateLimited = false);
            }
            return this;
        }

        @Override
        public Builder rateLimiterName(String rateLimiterName) {
            Assert.state(anyRequestConfigured || !requestMatcherConfigs.isEmpty(),
                "rateLimiterName() can only be called after requestMatchers() or anyRequest()");
            requestMatcherConfigs.stream()
                .skip(lastIndex)
                .forEach(config -> config.rateLimiterName = rateLimiterName);
            return this;
        }

        @Override
        public Builder rateLimited() {
            Assert.state(anyRequestConfigured || !requestMatcherConfigs.isEmpty(),
                "rateLimited() can only be called after requestMatchers() or anyRequest())");
            if (anyRequestConfigured) {
                this.defaultRateLimited = true;
            } else {
                requestMatcherConfigs.stream()
                    .skip(lastIndex)
                    .forEach(config -> config.rateLimited = true);
            }
            return this;
        }

        @Override
        public Builder order(int order) {
            this.order = order;
            return this;
        }

        @Override
        public RateLimitingFilter build() {
            return new RateLimitingFilter(rateLimiterRegistry,
                rateLimitExceptionHandler,
                requestMatcherConfigs, defaultRateLimited, order);
        }

        @Override
        protected Builder chainRequestMatchers(List<RequestMatcher> requestMatchers) {
            this.requestMatchers(requestMatchers.toArray(new RequestMatcher[0]));
            return this;
        }
    }
}