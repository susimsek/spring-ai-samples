package io.github.susimsek.springaisamples.versioning;


import io.github.susimsek.springaisamples.enums.FilterOrder;
import io.github.susimsek.springaisamples.exception.versioning.ApiVersionExceptionHandler;
import io.github.susimsek.springaisamples.exception.versioning.UnsupportedApiVersionException;
import io.github.susimsek.springaisamples.utils.ApiVersionUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
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
public class ApiVersionFilter extends OncePerRequestFilter implements Ordered {

    private final ApiVersionExceptionHandler apiVersionExceptionHandler;
    private final List<RequestMatcherConfig> requestMatcherConfigs;
    private final boolean defaultVersioned;
    private final int order;
    private final List<String> supportedVersions;

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        return requestMatcherConfigs.stream()
            .filter(config -> config.requestMatcher.matches(request))
            .map(config -> !config.versioned)
            .findFirst()
            .orElse(!defaultVersioned);
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
        throws ServletException, IOException {
        String uri = request.getRequestURI();
        String apiVersion = ApiVersionUtil.getVersionFromUri(uri);

        if (!isSupportedVersion(apiVersion)) {
            handleUnsupportedApiVersionException(request, response, apiVersion);
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private void handleUnsupportedApiVersionException(HttpServletRequest request,
                                                      HttpServletResponse response,
                                                      String apiVersion) throws ServletException, IOException {
        apiVersionExceptionHandler.handleUnsupportedApiVersionException(
            request, response, new UnsupportedApiVersionException(
                "Unsupported API version: " + apiVersion));
    }


    private boolean isSupportedVersion(String apiVersion) {
        return supportedVersions.contains(apiVersion);
    }

    @AllArgsConstructor
    private static class RequestMatcherConfig {
        private final RequestMatcher requestMatcher;
        private boolean versioned;
    }

    public interface InitialBuilder {
        InitialBuilder order(int order);

        InitialBuilder supportedVersions(String... versions);

        AfterRequestMatchersBuilder anyRequest();

        AfterRequestMatchersBuilder requestMatchers(HttpMethod method, String... patterns);

        AfterRequestMatchersBuilder requestMatchers(String... patterns);

        AfterRequestMatchersBuilder requestMatchers(RequestMatcher... requestMatchers);

        ApiVersionFilter build();
    }

    public interface AfterRequestMatchersBuilder {
        InitialBuilder permitAll();

        InitialBuilder versioned();
    }

    public static InitialBuilder builder(ApiVersionExceptionHandler apiVersionExceptionHandler) {
        return new Builder(apiVersionExceptionHandler);
    }

    private static class Builder extends AbstractRequestMatcherRegistry<Builder>
        implements InitialBuilder, AfterRequestMatchersBuilder {
        private final ApiVersionExceptionHandler apiVersionExceptionHandler;
        private final List<RequestMatcherConfig> requestMatcherConfigs = new ArrayList<>();
        private boolean anyRequestConfigured = false;
        private boolean defaultVersioned = true;
        private int order = FilterOrder.API_VERSION.order();
        private int lastIndex = 0;
        private final List<String> supportedVersions = new ArrayList<>();

        private Builder(ApiVersionExceptionHandler apiVersionExceptionHandler) {
            this.apiVersionExceptionHandler = apiVersionExceptionHandler;
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
                this.defaultVersioned = false;
            } else {
                requestMatcherConfigs.stream()
                    .skip(lastIndex)
                    .forEach(config -> config.versioned = false);
            }
            return this;
        }

        public Builder versioned() {
            Assert.state(anyRequestConfigured || !requestMatcherConfigs.isEmpty(),
                "versioned() can only be called after requestMatchers() or anyRequest())");
            if (anyRequestConfigured) {
                this.defaultVersioned = true;
            } else {
                requestMatcherConfigs.stream()
                    .skip(lastIndex)
                    .forEach(config -> config.versioned = true);
            }
            return this;
        }

        public Builder order(int order) {
            this.order = order;
            return this;
        }

        public Builder supportedVersions(String... versions) {
            this.supportedVersions.clear();
            this.supportedVersions.addAll(List.of(versions));
            return this;
        }

        public ApiVersionFilter build() {
            return new ApiVersionFilter(apiVersionExceptionHandler,
                requestMatcherConfigs, defaultVersioned, order, supportedVersions);
        }

        @Override
        protected Builder chainRequestMatchers(List<RequestMatcher> requestMatchers) {
            this.requestMatchers(requestMatchers.toArray(new RequestMatcher[0]));
            return this;
        }
    }
}