package io.github.susimsek.springaisamples.security;

import static io.github.susimsek.springaisamples.security.SignatureConstants.JWS_SIGNATURE_HEADER_NAME;

import io.github.susimsek.springaisamples.exception.security.JwsException;
import io.github.susimsek.springaisamples.exception.security.MissingJwsException;
import io.github.susimsek.springaisamples.exception.security.SignatureExceptionHandler;
import io.github.susimsek.springaisamples.logging.utils.CachedBodyHttpServletRequestWrapper;
import io.github.susimsek.springaisamples.service.SignatureService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

@Slf4j
@RequiredArgsConstructor
public class SignatureVerificationFilter extends OncePerRequestFilter implements Ordered {

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
        String requestURI = request.getRequestURI();
        log.info("Request URI: {}", requestURI);
        for (RequestMatcherConfig config : requestMatcherConfigs) {
            log.info("Checking request matcher: {}", config.requestMatcher);
            if (config.requestMatcher.matches(request)) {
                log.info("Request matched with: {} (signed: {})", config.requestMatcher, config.signed);
                return !config.signed;
            }
        }
        log.info("Default signed setting: {}", defaultSigned);
        return !defaultSigned;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
        throws ServletException, IOException {
        if (!shouldNotFilter(request)) {
            Optional<String> optionalJwsToken = Optional.ofNullable(request.getHeader(JWS_SIGNATURE_HEADER_NAME));
            if (optionalJwsToken.isEmpty()) {
                signatureExceptionHandler.handle(
                    request, response, new MissingJwsException("JWS token is missing"));
                return;
            }
            CachedBodyHttpServletRequestWrapper wrappedRequest = new CachedBodyHttpServletRequestWrapper(request);
            String jwsToken = optionalJwsToken.get();
            String requestBody = new String(wrappedRequest.getBody(), StandardCharsets.UTF_8);
            try {
                signatureService.validateJws(jwsToken, requestBody);
                filterChain.doFilter(wrappedRequest, response);
            } catch (JwsException e) {
                log.error("Invalid JWS signature: {}", e.getMessage());
                signatureExceptionHandler.handle(wrappedRequest, response, e);
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }


    public interface InitialBuilder {
        InitialBuilder order(int order);

        AfterRequestMatchersBuilder anyRequest();

        AfterRequestMatchersBuilder requestMatchers(HttpMethod method, String... patterns);

        AfterRequestMatchersBuilder requestMatchers(String... patterns);

        AfterRequestMatchersBuilder requestMatchers(RequestMatcher... requestMatchers);

        SignatureVerificationFilter build();
    }

    public interface AfterRequestMatchersBuilder {
        InitialBuilder permitAll();

        InitialBuilder signed();
    }

    @AllArgsConstructor
    private static class RequestMatcherConfig {
        private final RequestMatcher requestMatcher;
        private boolean signed;
    }

    public static InitialBuilder builder(SignatureService signatureService, SignatureExceptionHandler signatureExceptionHandler) {
        return new Builder(signatureService, signatureExceptionHandler);
    }

    private static class Builder extends AbstractRequestMatcherRegistry<Builder>
        implements InitialBuilder, AfterRequestMatchersBuilder {

        private final SignatureService signatureService;
        private final SignatureExceptionHandler signatureExceptionHandler;
        private final List<RequestMatcherConfig> requestMatcherConfigs = new ArrayList<>();
        private boolean anyRequestConfigured = false;
        private boolean defaultSigned = true;
        private int order = Ordered.HIGHEST_PRECEDENCE;
        private int lastIndex = 0;

        private Builder(SignatureService signatureService, SignatureExceptionHandler signatureExceptionHandler) {
            this.signatureService = signatureService;
            this.signatureExceptionHandler = signatureExceptionHandler;
        }

        @Override
        public Builder requestMatchers(HttpMethod method, String... patterns) {
            lastIndex = requestMatcherConfigs.size();
            for (String pattern : patterns) {
                this.requestMatcherConfigs.add(new RequestMatcherConfig(new AntPathRequestMatcher(pattern, method.name()), true));
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

        public SignatureVerificationFilter build() {
            return new SignatureVerificationFilter(signatureService, signatureExceptionHandler, requestMatcherConfigs, defaultSigned, order);
        }

        @Override
        protected Builder chainRequestMatchers(List<RequestMatcher> requestMatchers) {
            this.requestMatchers(requestMatchers.toArray(new RequestMatcher[0]));
            return this;
        }
    }
}