package io.github.susimsek.springaisamples.security;

import io.github.susimsek.springaisamples.utils.SanitizationUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
public class XssFilter extends OncePerRequestFilter implements Ordered {

    private final SanitizationUtil sanitizationUtil;
    private final List<RequestMatcherConfig> requestMatcherConfigs;
    private final boolean defaultSanitized;
    private final int order;
    private final List<String> nonSanitizedHeaders;

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        return requestMatcherConfigs.stream()
            .filter(config -> config.requestMatcher.matches(request))
            .map(config -> !config.sanitized)
            .findFirst()
            .orElse(!defaultSanitized);
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        XssRequestWrapper wrappedRequest = new XssRequestWrapper(
            request, sanitizationUtil, nonSanitizedHeaders);
        filterChain.doFilter(wrappedRequest, response);
    }

    @AllArgsConstructor
    private static class RequestMatcherConfig {
        private final RequestMatcher requestMatcher;
        private boolean sanitized;
    }

    public interface InitialBuilder {
        InitialBuilder order(int order);

        InitialBuilder nonSanitizedHeaders(String... headerNames);

        AfterRequestMatchersBuilder anyRequest();

        AfterRequestMatchersBuilder requestMatchers(HttpMethod method, String... patterns);

        AfterRequestMatchersBuilder requestMatchers(String... patterns);

        AfterRequestMatchersBuilder requestMatchers(RequestMatcher... requestMatchers);
        XssFilter build();
    }

    public interface AfterRequestMatchersBuilder {
        InitialBuilder permitAll();

        InitialBuilder sanitized();
    }

    public static InitialBuilder builder(SanitizationUtil sanitizationUtil) {
        return new Builder(sanitizationUtil);
    }

    private static class Builder extends AbstractRequestMatcherRegistry<Builder>
        implements InitialBuilder, AfterRequestMatchersBuilder {

        private final SanitizationUtil sanitizationUtil;
        private final List<RequestMatcherConfig> requestMatcherConfigs = new ArrayList<>();
        private boolean anyRequestConfigured = false;
        private boolean defaultSanitized = true;
        private int order = Ordered.HIGHEST_PRECEDENCE;
        private int lastIndex = 0;
        private final List<String> nonSanitizedHeaders = new ArrayList<>();

        private Builder(SanitizationUtil sanitizationUtil) {
            this.sanitizationUtil = sanitizationUtil;
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
                this.requestMatcherConfigs.add(
                    new RequestMatcherConfig(new AntPathRequestMatcher(pattern), true));
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
                this.defaultSanitized = false;
            } else {
                requestMatcherConfigs.stream()
                    .skip(lastIndex)
                    .forEach(config -> config.sanitized = false);
            }
            return this;
        }

        public Builder sanitized() {
            Assert.state(anyRequestConfigured || !requestMatcherConfigs.isEmpty(),
                "sanitized() can only be called after requestMatchers() or anyRequest())");
            if (anyRequestConfigured) {
                this.defaultSanitized = true;
            } else {
                requestMatcherConfigs.stream()
                    .skip(lastIndex)
                    .forEach(config -> config.sanitized = true);
            }
            return this;
        }

        public Builder nonSanitizedHeaders(String... headerNames) {
            this.nonSanitizedHeaders.addAll(Arrays.asList(headerNames));
            return this;
        }

        public Builder order(int order) {
            this.order = order;
            return this;
        }

        public XssFilter build() {
            return new XssFilter(sanitizationUtil,
                requestMatcherConfigs, defaultSanitized, order, nonSanitizedHeaders);
        }

        @Override
        protected Builder chainRequestMatchers(List<RequestMatcher> requestMatchers) {
            this.requestMatchers(requestMatchers.toArray(new RequestMatcher[0]));
            return this;
        }
    }
}