package io.github.susimsek.springaisamples.validation;

import io.github.susimsek.springaisamples.enums.FilterOrder;
import io.github.susimsek.springaisamples.exception.Violation;
import io.github.susimsek.springaisamples.exception.header.HeaderConstraintViolationException;
import io.github.susimsek.springaisamples.exception.header.HeaderValidationExceptionHandler;
import io.github.susimsek.springaisamples.i18n.MessageContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.MessageInterpolator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
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

    private final MessageInterpolator messageInterpolator;
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
            defaultHeaderConfigs.forEach((key, value) ->
                currentHeaderConfigs.putIfAbsent(key, value)
            );
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
            violations.add(createViolation(
                "NotBlank",
                headerConfig.getNotBlankMessage(),
                headerConfig.getHeaderName(),
                headerValue,
                locale,
                null
            ));
        }

        if (headerValue != null) {
            if (headerValue.length() < headerConfig.getMin() || headerValue.length() > headerConfig.getMax()) {
                Map<String, Object> args = Map.of("min", headerConfig.getMin(), "max", headerConfig.getMax());
                violations.add(createViolation(
                    "Size",
                    headerConfig.getSizeMessage(),
                    headerConfig.getHeaderName(),
                    headerValue,
                    locale,
                    args
                ));
            }

            if (!headerValue.matches(headerConfig.getRegexp())) {
                violations.add(createViolation(
                    "Pattern",
                    headerConfig.getPatternMessage(),
                    headerConfig.getHeaderName(),
                    headerValue,
                    locale,
                    null
                ));
            }
        }

        return violations;
    }

    private Violation createViolation(
        String code,
        String messageTemplate,
        String headerName,
        Object invalidValue,
        Locale locale,
        Map<String, Object> parameters) {
        MessageContext context = new
            MessageContext(messageTemplate, parameters, invalidValue);
        String localizedMessage = messageInterpolator.interpolate(
            messageTemplate, context, locale);

        return new Violation(
            code,
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

        private String notBlankMessage = "{validation.field.notBlank}";
        private String sizeMessage = "{validation.field.size}";
        private String patternMessage = "{validation.field.pattern}";
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

        AfterHeaderConfigBuilder notBlankMessage(String message);

        AfterHeaderConfigBuilder sizeMessage(String message);

        AfterHeaderConfigBuilder patternMessage(String message);

        InitialBuilder validated();
    }

    public interface AfterRequestMatchersBuilder {
        InitialBuilder permitAll();

        InitialBuilder validated();

        InitialBuilder anyRequest();

        AfterHeaderConfigBuilder headerName(String headerName);
    }

    public static InitialBuilder builder(MessageInterpolator messageInterpolator,
                                         HeaderValidationExceptionHandler headerValidationExceptionHandler) {
        return new Builder(messageInterpolator, headerValidationExceptionHandler);
    }

    private static class Builder extends AbstractRequestMatcherRegistry<Builder>
        implements InitialBuilder, AfterRequestMatchersBuilder, AfterHeaderConfigBuilder {

        private final MessageInterpolator messageInterpolator;
        private final HeaderValidationExceptionHandler headerValidationExceptionHandler;
        private final List<RequestMatcherConfig> requestMatcherConfigs = new ArrayList<>();
        private boolean anyRequestConfigured = false;
        private boolean defaultValidated = true;
        private int order = FilterOrder.HEADER_VALIDATION.order();
        private int lastIndex = 0;
        private final Map<String, HeaderConfig> defaultHeaderConfigs = new HashMap<>();
        private String headerName = null;

        private Builder(MessageInterpolator messageInterpolator,
                        HeaderValidationExceptionHandler headerValidationExceptionHandler) {
            this.messageInterpolator = messageInterpolator;
            this.headerValidationExceptionHandler = headerValidationExceptionHandler;
        }

        @Override
        public Builder requestMatchers(HttpMethod method, String... patterns) {
            lastIndex = requestMatcherConfigs.size();
            for (String pattern : patterns) {
                this.requestMatcherConfigs.add(new RequestMatcherConfig(
                    new AntPathRequestMatcher(pattern, method.name()), true,
                    new HashMap<>()));
            }
            return this;
        }

        @Override
        public Builder requestMatchers(String... patterns) {
            lastIndex = requestMatcherConfigs.size();
            for (String pattern : patterns) {
                this.requestMatcherConfigs.add(new RequestMatcherConfig(
                    new AntPathRequestMatcher(pattern), true, new HashMap<>()));
            }
            return this;
        }

        @Override
        public Builder requestMatchers(RequestMatcher... requestMatchers) {
            lastIndex = requestMatcherConfigs.size();
            for (RequestMatcher requestMatcher : requestMatchers) {
                this.requestMatcherConfigs.add(new RequestMatcherConfig(
                    requestMatcher, true, new HashMap<>()));
            }
            return this;
        }

        @Override
        public Builder headerName(String headerName) {
            validateState();
            if (anyRequestConfigured) {
                this.headerName = headerName;
                HeaderConfig headerConfig = new HeaderConfig();
                headerConfig.setHeaderName(headerName);
                defaultHeaderConfigs.put(headerName, headerConfig);
            } else {
                requestMatcherConfigs.stream().skip(lastIndex).forEach(config -> {
                    this.headerName = headerName;
                    HeaderConfig headerConfig = new HeaderConfig();
                    headerConfig.setHeaderName(headerName);
                    config.headerConfigs.put(headerName, headerConfig);
                });
            }
            return this;
        }

        @Override
        public Builder min(int min) {
            validateState();
            applyHeaderConfig(headerConfig -> headerConfig.setMin(min));
            return this;
        }

        @Override
        public Builder max(int max) {
            validateState();
            applyHeaderConfig(headerConfig -> headerConfig.setMax(max));
            return this;
        }

        @Override
        public Builder notBlank() {
            validateState();
            applyHeaderConfig(headerConfig -> headerConfig.setNotBlank(true));
            return this;
        }

        @Override
        public Builder regexp(String regexp) {
            validateState();
            applyHeaderConfig(headerConfig -> headerConfig.setRegexp(regexp));
            return this;
        }

        @Override
        public Builder notBlankMessage(String message) {
            validateState();
            applyHeaderConfig(headerConfig -> headerConfig.setNotBlankMessage(message));
            return this;
        }

        @Override
        public Builder sizeMessage(String message) {
            validateState();
            applyHeaderConfig(headerConfig -> headerConfig.setSizeMessage(message));
            return this;
        }

        @Override
        public Builder patternMessage(String message) {
            validateState();
            applyHeaderConfig(headerConfig -> headerConfig.setPatternMessage(message));
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
            return new HeaderValidationFilter(messageInterpolator,
                headerValidationExceptionHandler,
                requestMatcherConfigs, defaultValidated, order, defaultHeaderConfigs);
        }

        @Override
        protected Builder chainRequestMatchers(List<RequestMatcher> requestMatchers) {
            this.requestMatchers(requestMatchers.toArray(new RequestMatcher[0]));
            return this;
        }

        private void validateState() {
            Assert.state(anyRequestConfigured || !requestMatcherConfigs.isEmpty(),
                "This method can only be called after requestMatchers() or anyRequest()");
        }

        private void applyHeaderConfig(Consumer<HeaderConfig> consumer) {
            if (anyRequestConfigured) {
                if (!CollectionUtils.isEmpty(defaultHeaderConfigs)) {
                    HeaderConfig headerConfig = defaultHeaderConfigs.get(headerName);
                    consumer.accept(headerConfig);
                }
            } else {
                requestMatcherConfigs.stream().skip(lastIndex).forEach(config -> {
                    HeaderConfig headerConfig = config.headerConfigs.get(headerName);
                    consumer.accept(headerConfig);
                });
            }
        }
    }
}
