package io.github.susimsek.springaisamples.trace;

import static io.github.susimsek.springaisamples.trace.TraceConstants.CORRELATION_ID_HEADER_NAME;
import static io.github.susimsek.springaisamples.trace.TraceConstants.REQUEST_ID_HEADER_NAME;

import io.github.susimsek.springaisamples.enums.FilterOrder;
import io.github.susimsek.springaisamples.exception.trace.TraceExceptionHandler;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.security.config.annotation.web.AbstractRequestMatcherRegistry;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class TraceFilter extends OncePerRequestFilter implements Ordered {

    private final Tracer tracer;
    private final TraceExceptionHandler traceExceptionHandler;
    private final List<RequestMatcherConfig> requestMatcherConfigs;
    private final boolean defaultTraced;
    private final int order;

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        return requestMatcherConfigs.stream()
            .filter(config -> config.requestMatcher.matches(request))
            .map(config -> !config.traced)
            .findFirst()
            .orElse(!defaultTraced);
    }

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain)
        throws ServletException, IOException {
        String requestId = request.getHeader(REQUEST_ID_HEADER_NAME);
        if (!StringUtils.hasText(requestId)) {
            filterChain.doFilter(request, response);
            return;
        }

        String correlationId = request.getHeader(CORRELATION_ID_HEADER_NAME);
        if (!StringUtils.hasText(correlationId)) {
            filterChain.doFilter(request, response);
            return;
        }

        Span currentSpan = tracer.currentSpan();
        if (currentSpan != null) {
            currentSpan.tag("request.id", requestId);
            currentSpan.tag("correlation.id", correlationId);
        }

        MDC.put("requestId", requestId);
        MDC.put("correlationId", correlationId);
        response.setHeader(REQUEST_ID_HEADER_NAME, requestId);
        response.setHeader(CORRELATION_ID_HEADER_NAME, correlationId);
        String traceId = MDC.get("traceId");
        String spanId = MDC.get("spanId");

        Trace trace = Trace.builder()
            .traceId(traceId)
            .spanId(spanId)
            .requestId(requestId)
            .correlationId(correlationId)
            .build();
        TraceContextHolder.setTrace(trace);

        try {
            filterChain.doFilter(request, response);
        } finally {
            TraceContextHolder.clear();
            MDC.clear();
        }
    }

    @AllArgsConstructor
    private static class RequestMatcherConfig {
        private final RequestMatcher requestMatcher;
        private boolean traced;
    }

    public interface InitialBuilder {
        InitialBuilder order(int order);

        AfterRequestMatchersBuilder anyRequest();

        AfterRequestMatchersBuilder requestMatchers(HttpMethod method, String... patterns);

        AfterRequestMatchersBuilder requestMatchers(String... patterns);

        AfterRequestMatchersBuilder requestMatchers(RequestMatcher... requestMatchers);

        TraceFilter build();
    }

    public interface AfterRequestMatchersBuilder {
        InitialBuilder permitAll();

        InitialBuilder traced();
    }

    public static InitialBuilder builder(Tracer tracer,
                                         TraceExceptionHandler traceExceptionHandler) {
        return new Builder(tracer, traceExceptionHandler);
    }

    private static class Builder extends AbstractRequestMatcherRegistry<Builder>
        implements InitialBuilder, AfterRequestMatchersBuilder {

        private final Tracer tracer;
        private final TraceExceptionHandler traceExceptionHandler;
        private final List<RequestMatcherConfig> requestMatcherConfigs = new ArrayList<>();
        private boolean anyRequestConfigured = false;
        private boolean defaultTraced = true;
        private int order = FilterOrder.TRACE.order();
        private int lastIndex = 0;

        private Builder(Tracer tracer,
                        TraceExceptionHandler traceExceptionHandler) {
            this.tracer = tracer;
            this.traceExceptionHandler = traceExceptionHandler;
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
                this.defaultTraced = false;
            } else {
                requestMatcherConfigs.stream()
                    .skip(lastIndex)
                    .forEach(config -> config.traced = false);
            }
            return this;
        }

        public Builder traced() {
            Assert.state(anyRequestConfigured || !requestMatcherConfigs.isEmpty(),
                "traced() can only be called after requestMatchers() or anyRequest())");
            if (anyRequestConfigured) {
                this.defaultTraced = true;
            } else {
                requestMatcherConfigs.stream()
                    .skip(lastIndex)
                    .forEach(config -> config.traced = true);
            }
            return this;
        }

        public Builder order(int order) {
            this.order = order;
            return this;
        }

        public TraceFilter build() {
            return new TraceFilter(tracer,
                traceExceptionHandler,
                requestMatcherConfigs, defaultTraced, order);
        }

        @Override
        protected Builder chainRequestMatchers(List<RequestMatcher> requestMatchers) {
            this.requestMatchers(requestMatchers.toArray(new RequestMatcher[0]));
            return this;
        }
    }
}