package io.github.susimsek.springaisamples.idempotency;

import static io.github.susimsek.springaisamples.idempotency.IdempotencyConstants.IDEMPOTENCY_HEADER_NAME;

import io.github.susimsek.springaisamples.exception.idempotency.IdempotencyExceptionHandler;
import io.github.susimsek.springaisamples.exception.idempotency.MissingIdempotencyKeyException;
import io.github.susimsek.springaisamples.logging.utils.CachedBodyHttpServletResponseWrapper;
import io.github.susimsek.springaisamples.service.IdempotencyService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
public class IdempotencyFilter extends OncePerRequestFilter implements Ordered {

    private final IdempotencyService idempotencyService;
    private final IdempotencyExceptionHandler idempotencyExceptionHandler;
    private final List<IdempotencyFilter.RequestMatcherConfig> requestMatcherConfigs;
    private final boolean defaultSigned;
    private final int order;

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        return requestMatcherConfigs.stream()
            .filter(config -> config.requestMatcher.matches(request))
            .map(config -> !config.idempotent)
            .findFirst()
            .orElse(!defaultSigned);
    }

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain) throws ServletException, IOException {
        Optional<String> optionalJwsToken = Optional.ofNullable(request.getHeader(IDEMPOTENCY_HEADER_NAME));
        if (optionalJwsToken.isEmpty()) {
            handleMissingIdempotencyKey(request, response);
            return;
        }

        String idempotencyKey = optionalJwsToken.get();

        if (idempotencyService.containsKey(idempotencyKey)) {
            CachedResponse cachedResponse = idempotencyService.getResponse(idempotencyKey);
            response.setStatus(cachedResponse.status());
            cachedResponse.headers().forEach(response::setHeader);
            response.getWriter().write(cachedResponse.body());
            response.flushBuffer();
            return;
        }

        CachedBodyHttpServletResponseWrapper responseWrapper = new CachedBodyHttpServletResponseWrapper(response);
        filterChain.doFilter(request, responseWrapper);

        String responseBody = new String(responseWrapper.getBody(), StandardCharsets.UTF_8);
        int status = responseWrapper.getStatus();

        Map<String, String> headers = new HashMap<>();
        responseWrapper.getHeaderNames().forEach(
            headerName -> headers.put(headerName, responseWrapper.getHeader(headerName)));

        CachedResponse cachedResponse = new CachedResponse(status, headers, responseBody);
        idempotencyService.saveResponse(idempotencyKey, cachedResponse);

        response.setStatus(status);
        headers.forEach(response::setHeader);
        response.getWriter().write(responseBody);
        response.flushBuffer();
    }

    private void handleMissingIdempotencyKey(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        idempotencyExceptionHandler.handle(
            request, response, new MissingIdempotencyKeyException("Idempotency Key is missing"));
    }

    @AllArgsConstructor
    private static class RequestMatcherConfig {
        private final RequestMatcher requestMatcher;
        private boolean idempotent;
    }

    public interface InitialBuilder {
        InitialBuilder order(int order);

        AfterRequestMatchersBuilder anyRequest();

        AfterRequestMatchersBuilder requestMatchers(HttpMethod method, String... patterns);

        AfterRequestMatchersBuilder requestMatchers(String... patterns);

        AfterRequestMatchersBuilder requestMatchers(RequestMatcher... requestMatchers);

        IdempotencyFilter build();
    }

    public interface AfterRequestMatchersBuilder {
        InitialBuilder permitAll();

        InitialBuilder idempotent();
    }

    public static InitialBuilder builder(IdempotencyService idempotencyService,
                                         IdempotencyExceptionHandler idempotencyExceptionHandler) {
        return new Builder(idempotencyService, idempotencyExceptionHandler);
    }

    private static class Builder extends AbstractRequestMatcherRegistry<IdempotencyFilter.Builder>
        implements InitialBuilder, AfterRequestMatchersBuilder {

        private final IdempotencyService idempotencyService;
        private final IdempotencyExceptionHandler idempotencyExceptionHandler;
        private final List<IdempotencyFilter.RequestMatcherConfig> requestMatcherConfigs = new ArrayList<>();
        private boolean anyRequestConfigured = false;
        private boolean defaultIdempotent = true;
        private int order = Ordered.HIGHEST_PRECEDENCE;
        private int lastIndex = 0;

        private Builder(IdempotencyService idempotencyService,
                        IdempotencyExceptionHandler idempotencyExceptionHandler) {
            this.idempotencyService = idempotencyService;
            this.idempotencyExceptionHandler = idempotencyExceptionHandler;
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
                this.defaultIdempotent = false;
            } else {
                requestMatcherConfigs.stream()
                    .skip(lastIndex)
                    .forEach(config -> config.idempotent = false);
            }
            return this;
        }

        public Builder idempotent() {
            Assert.state(anyRequestConfigured || !requestMatcherConfigs.isEmpty(),
                "idempotent() can only be called after requestMatchers() or anyRequest())");
            if (anyRequestConfigured) {
                this.defaultIdempotent = true;
            } else {
                requestMatcherConfigs.stream()
                    .skip(lastIndex)
                    .forEach(config -> config.idempotent = true);
            }
            return this;
        }

        public Builder order(int order) {
            this.order = order;
            return this;
        }

        public IdempotencyFilter build() {
            return new IdempotencyFilter(idempotencyService,
                idempotencyExceptionHandler,
                requestMatcherConfigs, defaultIdempotent, order);
        }

        @Override
        protected Builder chainRequestMatchers(List<RequestMatcher> requestMatchers) {
            this.requestMatchers(requestMatchers.toArray(new RequestMatcher[0]));
            return this;
        }
    }
}