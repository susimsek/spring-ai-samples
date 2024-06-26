package io.github.susimsek.springaisamples.security.encryption;

import io.github.susimsek.springaisamples.enums.FilterOrder;
import io.github.susimsek.springaisamples.exception.encryption.EncryptionExceptionHandler;
import io.github.susimsek.springaisamples.exception.encryption.JweException;
import io.github.susimsek.springaisamples.exception.encryption.MissingJweException;
import io.github.susimsek.springaisamples.model.DecryptRequest;
import io.github.susimsek.springaisamples.service.EncryptionService;
import io.github.susimsek.springaisamples.utils.CachedBodyHttpServletRequestWrapper;
import io.github.susimsek.springaisamples.utils.JsonUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class DecryptionFilter extends OncePerRequestFilter implements Ordered {

    private final EncryptionService encryptionService;
    private final EncryptionExceptionHandler encryptionExceptionHandler;
    private final JsonUtil jsonUtil;
    private final List<RequestMatcherConfig> requestMatcherConfigs;
    private final boolean defaultDecrypted;
    private final int order;

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        return requestMatcherConfigs.stream()
            .filter(config -> config.requestMatcher.matches(request))
            .map(config -> !config.decrypted)
            .findFirst()
            .orElse(!defaultDecrypted);
    }

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        // Request decryption
        CachedBodyHttpServletRequestWrapper requestWrapper = new CachedBodyHttpServletRequestWrapper(request);
        String requestBody = requestWrapper.getContentAsString();

        DecryptRequest decryptRequest;
        try {
            decryptRequest = jsonUtil.convertToObject(requestBody, DecryptRequest.class);
        } catch (IOException e) {
            handleMissingJwe(request, response);
            return;
        }

        String encryptedBody = decryptRequest.jweToken();
        if (!StringUtils.hasText(encryptedBody)) {
            handleMissingJwe(request, response);
            return;
        }

        String decryptedBody;
        try {
            var data = encryptionService.decryptData(encryptedBody);
            decryptedBody = jsonUtil.convertObjectToString(data);

        } catch (JweException e) {
            handleJweException(request, response, e);
            return;
        }

        requestWrapper.setBody(decryptedBody.getBytes(StandardCharsets.UTF_8));

        filterChain.doFilter(requestWrapper, response);
    }

    private void handleJweException(HttpServletRequest request, HttpServletResponse response,
                                            JweException e)
        throws IOException, ServletException {
        encryptionExceptionHandler.handle(request, response, e);
    }

    private void handleMissingJwe(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException {
        encryptionExceptionHandler.handle(request, response, new MissingJweException("JWE token is missing"));
    }

    @AllArgsConstructor
    private static class RequestMatcherConfig {
        private final RequestMatcher requestMatcher;
        private boolean decrypted;
    }

    public interface InitialBuilder {
        InitialBuilder order(int order);

        AfterRequestMatchersBuilder anyRequest();

        AfterRequestMatchersBuilder requestMatchers(HttpMethod method, String... patterns);

        AfterRequestMatchersBuilder requestMatchers(String... patterns);

        AfterRequestMatchersBuilder requestMatchers(RequestMatcher... requestMatchers);

        DecryptionFilter build();
    }

    public interface AfterRequestMatchersBuilder {
        InitialBuilder permitAll();

        InitialBuilder decrypted();
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
        private boolean defaultDecrypted = true;
        private int order = FilterOrder.DECRYPTION.order();
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
                this.defaultDecrypted = false;
            } else {
                requestMatcherConfigs.stream()
                    .skip(lastIndex)
                    .forEach(config -> config.decrypted = false);
            }
            return this;
        }

        public Builder decrypted() {
            Assert.state(anyRequestConfigured || !requestMatcherConfigs.isEmpty(),
                "decrypted() can only be called after requestMatchers() or anyRequest())");
            if (anyRequestConfigured) {
                this.defaultDecrypted = true;
            } else {
                requestMatcherConfigs.stream()
                    .skip(lastIndex)
                    .forEach(config -> config.decrypted = true);
            }
            return this;
        }

        public Builder order(int order) {
            this.order = order;
            return this;
        }

        public DecryptionFilter build() {
            return new DecryptionFilter(encryptionService, encryptionExceptionHandler,
                jsonUtil, requestMatcherConfigs, defaultDecrypted, order);
        }

        @Override
        protected Builder chainRequestMatchers(List<RequestMatcher> requestMatchers) {
            this.requestMatchers(requestMatchers.toArray(new RequestMatcher[0]));
            return this;
        }
    }
}