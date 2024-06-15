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
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@Slf4j
@RequiredArgsConstructor
public class SignatureVerificationFilter extends OncePerRequestFilter {

    private final SignatureService signatureService;
    private final List<RequestMatcher> requestMatchers;
    private final SignatureExceptionHandler signatureExceptionHandler;

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
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
        filterChain.doFilter(wrappedRequest, wrappedResponse);
        String jwsToken = optionalJwsToken.get();
        byte[] requestBodyBytes = wrappedRequest.getContentAsByteArray();
        String requestBody = new String(requestBodyBytes, StandardCharsets.UTF_8);
        try {
            signatureService.validateJws(jwsToken, requestBody);
        } catch (JwsException e) {
            log.error("Invalid JWS signature: {}", e.getMessage());
            signatureExceptionHandler.handle(request, response, e);
            return;
        }
        wrappedResponse.copyBodyToResponse();
    }
}