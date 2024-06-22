package io.github.susimsek.springaisamples.logging.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

public class HttpRequestMatcher implements RequestMatcher {

    private final String pattern;
    private final HttpMethod httpMethod;
    private final PathMatcher pathMatcher;

    private HttpRequestMatcher(Builder builder) {
        this.pattern = builder.pattern;
        this.httpMethod = builder.httpMethod;
        this.pathMatcher = new AntPathMatcher();
    }

    @Override
    public boolean matches(HttpServletRequest request) {
        return matchesPattern(request.getRequestURI()) && matchesMethod(HttpMethod.valueOf(request.getMethod()));
    }

    public boolean matches(HttpRequest request) {
        return matchesPattern(request.getURI().getPath()) && matchesMethod(request.getMethod());
    }

    private boolean matchesPattern(String uri) {
        return pathMatcher.match(pattern, uri);
    }

    private boolean matchesMethod(HttpMethod method) {
        return httpMethod == null || httpMethod.equals(method);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String pattern;
        private HttpMethod httpMethod;

        public Builder pattern(String pattern) {
            this.pattern = pattern;
            return this;
        }

        public Builder pattern(HttpMethod method, String pattern) {
            this.httpMethod = method;
            this.pattern = pattern;
            return this;
        }

        public HttpRequestMatcher build() {
            return new HttpRequestMatcher(this);
        }
    }
}