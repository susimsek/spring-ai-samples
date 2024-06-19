package io.github.susimsek.springaisamples.ratelimit;

import static io.github.susimsek.springaisamples.ratelimit.RateLimitConstants.RATE_LIMIT_LIMIT_HEADER_NAME;
import static io.github.susimsek.springaisamples.ratelimit.RateLimitConstants.RATE_LIMIT_REMAINING_HEADER_NAME;
import static io.github.susimsek.springaisamples.ratelimit.RateLimitConstants.RATE_LIMIT_RESET_HEADER_NAME;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.susimsek.springaisamples.exception.ratelimit.RateLimitExceededException;
import io.github.susimsek.springaisamples.exception.ratelimit.RateLimitExceptionHandler;
import io.github.susimsek.springaisamples.enums.FilterOrder;
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
public class RateLimitFilter extends OncePerRequestFilter implements Ordered {

    private final RateLimiter rateLimiter;
    private final RateLimitExceptionHandler rateLimitExceptionHandler;
    private final List<RequestMatcherConfig> requestMatcherConfigs;
    private final boolean defaultRateLimited;
    private final int order;

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        return requestMatcherConfigs.stream()
            .filter(config -> config.requestMatcher.matches(request))
            .map(config -> !config.rateLimited)
            .findFirst()
            .orElse(!defaultRateLimited);
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
        throws ServletException, IOException {
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
        String errorMessage = "Too many requests";
        RateLimitExceededException exception = new RateLimitExceededException(errorMessage,
            limitForPeriod, availablePermissions, resetTime);
        rateLimitExceptionHandler.handle(request, response, exception);
    }

    @AllArgsConstructor
    private static class RequestMatcherConfig {
        private final RequestMatcher requestMatcher;
        private boolean rateLimited;
    }

    public interface InitialBuilder {
        InitialBuilder order(int order);

        AfterRequestMatchersBuilder anyRequest();

        AfterRequestMatchersBuilder requestMatchers(HttpMethod method, String... patterns);

        AfterRequestMatchersBuilder requestMatchers(String... patterns);

        AfterRequestMatchersBuilder requestMatchers(RequestMatcher... requestMatchers);

        RateLimitFilter build();
    }

    public interface AfterRequestMatchersBuilder {
        InitialBuilder permitAll();

        InitialBuilder rateLimited();
    }

    public static InitialBuilder builder(RateLimiterRegistry rateLimiterRegistry,
                                         RateLimitExceptionHandler rateLimitExceptionHandler) {
        return new Builder(rateLimiterRegistry, rateLimitExceptionHandler);
    }

    private static class Builder extends AbstractRequestMatcherRegistry<Builder>
        implements InitialBuilder, AfterRequestMatchersBuilder {

        private final RateLimiter rateLimiter;
        private final RateLimitExceptionHandler rateLimitExceptionHandler;
        private final List<RequestMatcherConfig> requestMatcherConfigs = new ArrayList<>();
        private boolean anyRequestConfigured = false;
        private boolean defaultRateLimited = true;
        private int order = FilterOrder.RATE_LIMIT.order();
        private int lastIndex = 0;

        private Builder(RateLimiterRegistry rateLimiterRegistry,
                        RateLimitExceptionHandler rateLimitExceptionHandler) {
            this.rateLimiter = rateLimiterRegistry.rateLimiter("default");
            this.rateLimitExceptionHandler = rateLimitExceptionHandler;
        }

        @Override
        public Builder requestMatchers(HttpMethod method, String... patterns) {
            lastIndex = requestMatcherConfigs.size();
            for (String pattern : patterns) {
                this.requestMatcherConfigs.add(new RequestMatcherConfig(
                    new AntPathRequestMatcher(pattern, method.name()), true));
            }
            return this;
        }

        @Override
        public Builder requestMatchers(String... patterns) {
            lastIndex = requestMatcherConfigs.size();
            for (String pattern : patterns) {
                this.requestMatcherConfigs.add(new RequestMatcherConfig(new AntPathRequestMatcher(pattern), true));
            }
            return this;
        }

        @Override
        public Builder requestMatchers(RequestMatcher... requestMatchers) {
            lastIndex = requestMatcherConfigs.size();
            for (RequestMatcher requestMatcher : requestMatchers) {
                this.requestMatcherConfigs.add(new RequestMatcherConfig(requestMatcher, true));
            }
            return this;
        }

        @Override
        public Builder anyRequest() {
            Assert.state(!this.anyRequestConfigured, "anyRequest() can only be called once");
            this.anyRequestConfigured = true;
            return this;
        }

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

        public Builder order(int order) {
            this.order = order;
            return this;
        }

        public RateLimitFilter build() {
            return new RateLimitFilter(rateLimiter,
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