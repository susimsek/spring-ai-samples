package io.github.susimsek.springaisamples.security;

import static io.github.susimsek.springaisamples.security.SignatureConstants.JWS_SIGNATURE_HEADER_NAME;

import io.github.susimsek.springaisamples.exception.security.JwsException;
import io.github.susimsek.springaisamples.exception.security.MissingJwsException;
import io.github.susimsek.springaisamples.exception.security.SignatureExceptionHandler;
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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.lang.NonNull;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.StreamUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@RequiredArgsConstructor
public class SignatureVerificationFilter extends OncePerRequestFilter implements Ordered {

    private final SignatureService signatureService;
    private final SignatureExceptionHandler signatureExceptionHandler;
    private final List<RequestMatcher> requestMatchers;
    private final int order;

    @Override
    public int getOrder() {
        return order;
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        return requestMatchers.stream().noneMatch(matcher -> matcher.matches(request));
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
        throws ServletException, IOException {
        Optional<String> optionalJwsToken = Optional.ofNullable(request.getHeader(JWS_SIGNATURE_HEADER_NAME));
        if (optionalJwsToken.isEmpty()) {
            signatureExceptionHandler.handle(
                request, response, new MissingJwsException("JWS token is missing"));
            return;
        }
        String jwsToken = optionalJwsToken.get();
        String requestBody = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
        try {
            signatureService.validateJws(jwsToken, requestBody);
            filterChain.doFilter(request, response);
        } catch (JwsException e) {
            log.error("Invalid JWS signature: {}", e.getMessage());
            signatureExceptionHandler.handle(request, response, e);
        }
    }

    public static Builder builder(SignatureService signatureService, SignatureExceptionHandler signatureExceptionHandler) {
        return new Builder(signatureService, signatureExceptionHandler);
    }

    public static class Builder {

        private final SignatureService signatureService;
        private final SignatureExceptionHandler signatureExceptionHandler;
        private final List<RequestMatcher> requestMatchers = new ArrayList<>();
        private int order = Ordered.HIGHEST_PRECEDENCE;

        private Builder(SignatureService signatureService, SignatureExceptionHandler signatureExceptionHandler) {
            this.signatureService = signatureService;
            this.signatureExceptionHandler = signatureExceptionHandler;
        }

        public Builder requestMatchers(HttpMethod method, String... patterns) {
            for (String pattern : patterns) {
                this.requestMatchers.add(new AntPathRequestMatcher(pattern, method.name()));
            }
            return this;
        }

        public Builder requestMatchers(String... patterns) {
            for (String pattern : patterns) {
                this.requestMatchers.add(new AntPathRequestMatcher(pattern));
            }
            return this;
        }

        public Builder order(int order) {
            this.order = order;
            return this;
        }

        public SignatureVerificationFilter build() {
            return new SignatureVerificationFilter(signatureService, signatureExceptionHandler, requestMatchers, order);
        }
    }
}