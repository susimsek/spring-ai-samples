package io.github.susimsek.springaisamples.validation;

import io.github.susimsek.springaisamples.enums.FilterOrder;
import io.github.susimsek.springaisamples.exception.Violation;
import io.github.susimsek.springaisamples.exception.header.HeaderConstraintViolationException;
import io.github.susimsek.springaisamples.exception.header.HeaderValidationExceptionHandler;
import io.github.susimsek.springaisamples.i18n.ParameterMessageSource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.security.config.annotation.web.AbstractRequestMatcherRegistry;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class HeaderValidationFilter extends OncePerRequestFilter implements Ordered {

    private final ParameterMessageSource messageSource;
    private final HeaderValidationExceptionHandler headerValidationExceptionHandler;
    private final List<RequestMatcherConfig> requestMatcherConfigs;
    private final boolean defaultValidated;
    private final int order;
    private final Map<String, HeaderConfig> defaultHeaderConfigs;

    private Map<String, HeaderConfig> currentHeaderConfigs;

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        var optionalConfig = requestMatcherConfigs.stream()
            .filter(config -> config.requestMatcher.matches(request))
            .findFirst()
            .map(config -> {
                currentHeaderConfigs = config.headerConfigs;
                return !config.validated;
            });

        if (optionalConfig.isEmpty()) {
            currentHeaderConfigs = defaultHeaderConfigs;
            return !defaultValidated;
        } else {
            return optionalConfig.get();
        }
    }

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain)
        throws ServletException, IOException {

        List<Violation> violations = currentHeaderConfigs.values().stream()
            .flatMap(headerConfig -> {
                String headerValue = request.getHeader(headerConfig.getHeaderName());
                return validateHeader(headerConfig, headerValue, request.getLocale()).stream();
            })
            .toList();

        if (!violations.isEmpty()) {
            handleHeaderConstraintViolationException(request, response, violations);
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private List<Violation> validateHeader(HeaderConfig headerConfig, String headerValue, Locale locale) {
        List<Violation> violations = new ArrayList<>();

        if (headerConfig.isNotBlank() && !StringUtils.hasText(headerValue)) {
            violations.add(
                createViolation("validation.field.notBlank",
                    headerConfig.getHeaderName(), headerValue, locale, null));
        }

        if (headerValue != null) {
            if (headerValue.length() < headerConfig.getMin() || headerValue.length() > headerConfig.getMax()) {
                var args = Map.of("min", String.valueOf(headerConfig.getMin()),
                    "max", String.valueOf(headerConfig.getMax()));
                violations.add(
                    createViolation("validation.field.size", headerConfig.getHeaderName(), headerValue, locale, args));
            }

            if (!headerValue.matches(headerConfig.getRegexp())) {
                violations.add(
                    createViolation("validation.field.pattern",
                        headerConfig.getHeaderName(), headerValue, locale, null));
            }
        }

        return violations;
    }

    private Violation createViolation(String messageTemplate, String headerName, Object invalidValue, Locale locale,
                                      Map<String, String> args) {
        String localizedMessage = messageSource.getMessageWithNamedArgs(messageTemplate, args, locale);

        return new Violation(
            null,
            headerName,
            invalidValue,
            localizedMessage
        );
    }

    private void handleHeaderConstraintViolationException(
        HttpServletRequest request,
        HttpServletResponse response,
        List<Violation> violations) throws IOException, ServletException {
        headerValidationExceptionHandler.handleHeaderValidationException(request, response,
            new HeaderConstraintViolationException(violations));
    }

    @Getter
    @AllArgsConstructor
    private static class RequestMatcherConfig {
        private final RequestMatcher requestMatcher;
        private boolean validated;
        private final Map<String, HeaderConfig> headerConfigs;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @lombok.Builder
    private static class HeaderConfig {
        private String headerName;
        private int min = 0;
        private int max = Integer.MAX_VALUE;
        private boolean notBlank = false;
        private String regexp;
    }

    public interface InitialBuilder {
        InitialBuilder order(int order);

        AfterRequestMatchersBuilder anyRequest();

        AfterRequestMatchersBuilder requestMatchers(HttpMethod method, String... patterns);

        AfterRequestMatchersBuilder requestMatchers(String... patterns);

        AfterRequestMatchersBuilder requestMatchers(RequestMatcher... requestMatchers);

        HeaderValidationFilter build();
    }

    public interface AfterHeaderConfigBuilder {
        AfterHeaderConfigBuilder headerName(String headerName);

        AfterHeaderConfigBuilder min(int min);

        AfterHeaderConfigBuilder max(int max);

        AfterHeaderConfigBuilder notBlank();

        AfterHeaderConfigBuilder regexp(String regexp);

        InitialBuilder validated();
    }

    public interface AfterRequestMatchersBuilder {
        InitialBuilder permitAll();

        InitialBuilder validated();

        InitialBuilder anyRequest();

        AfterHeaderConfigBuilder headerName(String headerName);
    }

    public static InitialBuilder builder(ParameterMessageSource messageSource,
                                         HeaderValidationExceptionHandler headerValidationExceptionHandler) {
        return new Builder(messageSource, headerValidationExceptionHandler);
    }

    private static class Builder extends AbstractRequestMatcherRegistry<Builder>
        implements InitialBuilder, AfterRequestMatchersBuilder, AfterHeaderConfigBuilder {

        private final ParameterMessageSource messageSource;
        private final HeaderValidationExceptionHandler headerValidationExceptionHandler;
        private final List<RequestMatcherConfig> requestMatcherConfigs = new ArrayList<>();
        private boolean anyRequestConfigured = false;
        private boolean defaultValidated = true;
        private int order = FilterOrder.TRACE.order();
        private int lastIndex = 0;
        private final Map<String, HeaderConfig> defaultHeaderConfigs = new HashMap<>();
        private String headerName = null;

        private Builder(ParameterMessageSource messageSource,
                        HeaderValidationExceptionHandler headerValidationExceptionHandler) {
            this.messageSource = messageSource;
            this.headerValidationExceptionHandler = headerValidationExceptionHandler;
        }

        @Override
        public Builder requestMatchers(HttpMethod method, String... patterns) {
            lastIndex = requestMatcherConfigs.size();
            for (String pattern : patterns) {
                this.requestMatcherConfigs.add(new RequestMatcherConfig(
                    new AntPathRequestMatcher(pattern, method.name()), true,
                    Collections.emptyMap()));
            }
            return this;
        }

        @Override
        public Builder requestMatchers(String... patterns) {
            lastIndex = requestMatcherConfigs.size();
            for (String pattern : patterns) {
                this.requestMatcherConfigs.add(new RequestMatcherConfig(
                    new AntPathRequestMatcher(pattern), true, Collections.emptyMap()));
            }
            return this;
        }

        @Override
        public Builder requestMatchers(RequestMatcher... requestMatchers) {
            lastIndex = requestMatcherConfigs.size();
            for (RequestMatcher requestMatcher : requestMatchers) {
                this.requestMatcherConfigs.add(new RequestMatcherConfig(
                    requestMatcher, true, Collections.emptyMap()));
            }
            return this;
        }

        @Override
        public Builder headerName(String headerName) {
            Assert.state(anyRequestConfigured || !requestMatcherConfigs.isEmpty(),
                "headerName() can only be called after requestMatchers() or anyRequest()");
            if (anyRequestConfigured) {
                this.headerName = headerName;
                HeaderConfig headerConfig = new HeaderConfig();
                headerConfig.setHeaderName(headerName);
                defaultHeaderConfigs.put(headerName, headerConfig);
            } else {
                requestMatcherConfigs.stream()
                    .skip(lastIndex)
                    .forEach(config -> {
                        HeaderConfig headerConfig = new HeaderConfig();
                        headerConfig.setHeaderName(headerName);
                        config.headerConfigs.put(headerName, headerConfig);
                    });
            }
            return this;
        }

        @Override
        public Builder min(int min) {
            Assert.state(anyRequestConfigured || !requestMatcherConfigs.isEmpty(),
                "min() can only be called after requestMatchers() or anyRequest()");
            if (anyRequestConfigured) {
                if (!CollectionUtils.isEmpty(defaultHeaderConfigs)) {
                    HeaderConfig headerConfig = defaultHeaderConfigs.get(headerName);
                    headerConfig.setMin(min);
                }
            } else {
                requestMatcherConfigs.stream()
                    .skip(lastIndex)
                    .forEach(config -> {
                        HeaderConfig headerConfig = config.headerConfigs.get(headerName);
                        headerConfig.setMin(min);
                    });
            }
            return this;
        }

        @Override
        public Builder max(int max) {
            Assert.state(anyRequestConfigured || !requestMatcherConfigs.isEmpty(),
                "max() can only be called after requestMatchers() or anyRequest()");
            if (anyRequestConfigured) {
                if (!CollectionUtils.isEmpty(defaultHeaderConfigs)) {
                    HeaderConfig headerConfig = defaultHeaderConfigs.get(headerName);
                    headerConfig.setMax(max);
                }
            } else {
                requestMatcherConfigs.stream()
                    .skip(lastIndex)
                    .forEach(config -> {
                        HeaderConfig headerConfig = config.headerConfigs.get(headerName);
                        headerConfig.setMax(max);
                    });
            }
            return this;
        }

        @Override
        public Builder notBlank() {
            Assert.state(anyRequestConfigured || !requestMatcherConfigs.isEmpty(),
                "notBlank() can only be called after requestMatchers() or anyRequest()");
            if (anyRequestConfigured) {
                if (!CollectionUtils.isEmpty(defaultHeaderConfigs)) {
                    HeaderConfig headerConfig = defaultHeaderConfigs.get(headerName);
                    headerConfig.setNotBlank(true);
                }
            } else {
                requestMatcherConfigs.stream()
                    .skip(lastIndex)
                    .forEach(config -> {
                        HeaderConfig headerConfig = config.headerConfigs.get(headerName);
                        headerConfig.setNotBlank(true);
                    });
            }
            return this;
        }

        @Override
        public Builder regexp(String regexp) {
            Assert.state(anyRequestConfigured || !requestMatcherConfigs.isEmpty(),
                "regexp() can only be called after requestMatchers() or anyRequest()");
            if (anyRequestConfigured) {
                if (!CollectionUtils.isEmpty(defaultHeaderConfigs)) {
                    HeaderConfig headerConfig = defaultHeaderConfigs.get(headerName);
                    headerConfig.setRegexp(regexp);
                }
            } else {
                requestMatcherConfigs.stream()
                    .skip(lastIndex)
                    .forEach(config -> {
                        HeaderConfig headerConfig = config.headerConfigs.get(headerName);
                        headerConfig.setRegexp(regexp);
                    });
            }
            return this;
        }

        @Override
        public Builder validated() {
            Assert.state(anyRequestConfigured || !requestMatcherConfigs.isEmpty(),
                "validated() can only be called after requestMatchers() or anyRequest()");
            if (anyRequestConfigured) {
                this.defaultValidated = true;
            } else {
                requestMatcherConfigs.stream()
                    .skip(lastIndex)
                    .forEach(config -> config.validated = true);
            }
            this.headerName = null;
            return this;
        }

        @Override
        public Builder permitAll() {
            Assert.state(anyRequestConfigured || !requestMatcherConfigs.isEmpty(),
                "permitAll() can only be called after requestMatchers() or anyRequest()");
            if (anyRequestConfigured) {
                this.defaultValidated = false;
            } else {
                requestMatcherConfigs.stream()
                    .skip(lastIndex)
                    .forEach(config -> config.validated = false);
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
        public Builder order(int order) {
            this.order = order;
            return this;
        }

        @Override
        public HeaderValidationFilter build() {
            return new HeaderValidationFilter(messageSource,
                headerValidationExceptionHandler,
                requestMatcherConfigs, defaultValidated, order, defaultHeaderConfigs);
        }

        @Override
        protected Builder chainRequestMatchers(List<RequestMatcher> requestMatchers) {
            this.requestMatchers(requestMatchers.toArray(new RequestMatcher[0]));
            return this;
        }
    }
}