package io.github.susimsek.springaisamples.security.signature;

import static io.github.susimsek.springaisamples.security.signature.SignatureConstants.JWS_SIGNATURE_HEADER_NAME;

import io.github.susimsek.springaisamples.enums.FilterOrder;
import io.github.susimsek.springaisamples.exception.security.JwsException;
import io.github.susimsek.springaisamples.exception.security.SignatureExceptionHandler;
import io.github.susimsek.springaisamples.service.SignatureService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.security.config.annotation.web.AbstractRequestMatcherRegistry;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Slf4j
@RequiredArgsConstructor
public class SignatureFilter extends OncePerRequestFilter implements Ordered {

    private final SignatureService signatureService;
    private final SignatureExceptionHandler signatureExceptionHandler;
    private final List<RequestMatcherConfig> requestMatcherConfigs;
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
            .map(config -> !config.signed)
            .findFirst()
            .orElse(!defaultSigned);
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
        throws ServletException, IOException {
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);
        filterChain.doFilter(request, responseWrapper);
        String responseBody = new String(responseWrapper.getContentAsByteArray(), responseWrapper.getCharacterEncoding());

        try {
            String signature = signatureService.createJws(responseBody);
            responseWrapper.setHeader(JWS_SIGNATURE_HEADER_NAME, signature);
            responseWrapper.copyBodyToResponse();
        } catch (JwsException e) {
            handleJwsException(request, response, e);
        }
    }

    private void handleJwsException(HttpServletRequest request, HttpServletResponse response, JwsException e)
        throws IOException, ServletException {
        signatureExceptionHandler.handle(request, response, e);
    }

    @AllArgsConstructor
    private static class RequestMatcherConfig {
        private final RequestMatcher requestMatcher;
        private boolean signed;
    }

    public interface InitialBuilder {
        InitialBuilder order(int order);

        AfterRequestMatchersBuilder anyRequest();

        AfterRequestMatchersBuilder requestMatchers(HttpMethod method, String... patterns);

        AfterRequestMatchersBuilder requestMatchers(String... patterns);

        AfterRequestMatchersBuilder requestMatchers(RequestMatcher... requestMatchers);

        SignatureFilter build();
    }

    public interface AfterRequestMatchersBuilder {
        InitialBuilder permitAll();

        InitialBuilder signed();
    }

    public static InitialBuilder builder(SignatureService signatureService,
                                         SignatureExceptionHandler signatureExceptionHandler) {
        return new Builder(signatureService, signatureExceptionHandler);
    }

    private static class Builder extends AbstractRequestMatcherRegistry<Builder>
        implements InitialBuilder, AfterRequestMatchersBuilder {

        private final SignatureService signatureService;
        private final SignatureExceptionHandler signatureExceptionHandler;
        private final List<RequestMatcherConfig> requestMatcherConfigs = new ArrayList<>();
        private boolean anyRequestConfigured = false;
        private boolean defaultSigned = true;
        private int order = FilterOrder.SIGNATURE.order();
        private int lastIndex = 0;

        private Builder(SignatureService signatureService, SignatureExceptionHandler signatureExceptionHandler) {
            this.signatureService = signatureService;
            this.signatureExceptionHandler = signatureExceptionHandler;
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
                this.defaultSigned = false;
            } else {
                requestMatcherConfigs.stream()
                    .skip(lastIndex)
                    .forEach(config -> config.signed = false);
            }
            return this;
        }

        public Builder signed() {
            Assert.state(anyRequestConfigured || !requestMatcherConfigs.isEmpty(),
                "signed() can only be called after requestMatchers() or anyRequest())");
            if (anyRequestConfigured) {
                this.defaultSigned = true;
            } else {
                requestMatcherConfigs.stream()
                    .skip(lastIndex)
                    .forEach(config -> config.signed = true);
            }
            return this;
        }

        public Builder order(int order) {
            this.order = order;
            return this;
        }

        public SignatureFilter build() {
            return new SignatureFilter(signatureService, signatureExceptionHandler,
                requestMatcherConfigs, defaultSigned, order);
        }

        @Override
        protected Builder chainRequestMatchers(List<RequestMatcher> requestMatchers) {
            this.requestMatchers(requestMatchers.toArray(new RequestMatcher[0]));
            return this;
        }
    }
}