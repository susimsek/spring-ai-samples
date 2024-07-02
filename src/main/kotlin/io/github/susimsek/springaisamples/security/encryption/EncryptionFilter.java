package io.github.susimsek.springaisamples.security.encryption;

import io.github.susimsek.springaisamples.enums.FilterOrder;
import io.github.susimsek.springaisamples.exception.encryption.EncryptionExceptionHandler;
import io.github.susimsek.springaisamples.exception.encryption.JweException;
import io.github.susimsek.springaisamples.model.EncryptResponse;
import io.github.susimsek.springaisamples.service.EncryptionService;
import io.github.susimsek.springaisamples.utils.JsonUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.config.annotation.web.AbstractRequestMatcherRegistry;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

@RequiredArgsConstructor
@Slf4j
public class EncryptionFilter extends OncePerRequestFilter implements Ordered {

    private final EncryptionService encryptionService;
    private final EncryptionExceptionHandler encryptionExceptionHandler;
    private final JsonUtil jsonUtil;
    private final List<RequestMatcherConfig> requestMatcherConfigs;
    private final boolean defaultEncrypted;
    private final int order;

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        return requestMatcherConfigs.stream()
            .filter(config -> config.requestMatcher.matches(request))
            .map(config -> !config.encrypted)
            .findFirst()
            .orElse(!defaultEncrypted);
    }

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        filterChain.doFilter(request, responseWrapper);

        String responseBody = new String(responseWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
        String encryptedResponseBody;
        try {
            var jsonObject = jsonUtil.convertToJsonObject(responseBody);
            encryptedResponseBody = encryptionService.encryptData(jsonObject);
        } catch (JweException e) {
            handleJweException(request, response, e);
            return;
        }

        EncryptResponse encryptResponse = new EncryptResponse(encryptedResponseBody);
        Optional<URI> optionalUri = getUri(request);
        if (optionalUri.isPresent()) {
            URI uri = optionalUri.get();
            Link selfLink = Link.of(uri.toString()).withSelfRel().withType(request.getMethod());
            encryptResponse.add(selfLink);
            String baseUrl = uri.getScheme() + "://" + uri.getAuthority() + request.getContextPath();
            Link decryptLink = Link.of(baseUrl + "/api/v1/security/decrypt")
                .withRel("decrypt").withType(HttpMethod.POST.name());
            encryptResponse.add(decryptLink);
        }

        String encryptedResponseJson = jsonUtil.convertObjectToString(encryptResponse);

        responseWrapper.resetBuffer();
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        responseWrapper.setContentLength(encryptedResponseJson.length());
        responseWrapper.setContentType(MediaType.APPLICATION_JSON_VALUE);
        responseWrapper.getWriter().write(encryptedResponseJson);
        responseWrapper.copyBodyToResponse();
    }

    private Optional<URI> getUri(HttpServletRequest request) {
        try {
            return Optional.of(new URI(request.getRequestURL().toString()));
        } catch (URISyntaxException e) {
            log.error("Invalid URI Syntax for request: {}", request.getRequestURL(), e);
            return Optional.empty();
        }
    }

    private void handleJweException(HttpServletRequest request, HttpServletResponse response,
                                            JweException e)
        throws IOException, ServletException {
        encryptionExceptionHandler.handle(request, response, e);
    }

    @AllArgsConstructor
    private static class RequestMatcherConfig {
        private final RequestMatcher requestMatcher;
        private boolean encrypted;
    }

    public interface InitialBuilder {
        InitialBuilder order(int order);

        AfterRequestMatchersBuilder anyRequest();

        AfterRequestMatchersBuilder requestMatchers(HttpMethod method, String... patterns);

        AfterRequestMatchersBuilder requestMatchers(String... patterns);

        AfterRequestMatchersBuilder requestMatchers(RequestMatcher... requestMatchers);

        EncryptionFilter build();
    }

    public interface AfterRequestMatchersBuilder {
        InitialBuilder permitAll();

        InitialBuilder encrypted();
    }

    public static InitialBuilder builder(EncryptionService encryptionService,
                                         EncryptionExceptionHandler encryptionExceptionHandler,
                                         JsonUtil jsonUtil) {
        return new Builder(encryptionService, encryptionExceptionHandler, jsonUtil);
    }

    private static class Builder extends AbstractRequestMatcherRegistry<Builder>
        implements InitialBuilder, AfterRequestMatchersBuilder {

        private final EncryptionService encryptionService;
        private final EncryptionExceptionHandler encryptionExceptionHandler;
        private final JsonUtil jsonUtil;
        private final List<RequestMatcherConfig> requestMatcherConfigs = new ArrayList<>();
        private boolean anyRequestConfigured = false;
        private boolean defaultEncrypted = true;
        private int order = FilterOrder.ENCRYPTION.order();
        private int lastIndex = 0;

        private Builder(EncryptionService encryptionService,
                        EncryptionExceptionHandler encryptionExceptionHandler,
                        JsonUtil jsonUtil) {
            this.encryptionService = encryptionService;
            this.encryptionExceptionHandler = encryptionExceptionHandler;
            this.jsonUtil = jsonUtil;
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
                this.defaultEncrypted = false;
            } else {
                requestMatcherConfigs.stream()
                    .skip(lastIndex)
                    .forEach(config -> config.encrypted = false);
            }
            return this;
        }

        public Builder encrypted() {
            Assert.state(anyRequestConfigured || !requestMatcherConfigs.isEmpty(),
                "encrypted() can only be called after requestMatchers() or anyRequest())");
            if (anyRequestConfigured) {
                this.defaultEncrypted = true;
            } else {
                requestMatcherConfigs.stream()
                    .skip(lastIndex)
                    .forEach(config -> config.encrypted = true);
            }
            return this;
        }

        public Builder order(int order) {
            this.order = order;
            return this;
        }

        public EncryptionFilter build() {
            return new EncryptionFilter(encryptionService, encryptionExceptionHandler,
                jsonUtil, requestMatcherConfigs, defaultEncrypted, order);
        }

        @Override
        protected Builder chainRequestMatchers(List<RequestMatcher> requestMatchers) {
            this.requestMatchers(requestMatchers.toArray(new RequestMatcher[0]));
            return this;
        }
    }
}