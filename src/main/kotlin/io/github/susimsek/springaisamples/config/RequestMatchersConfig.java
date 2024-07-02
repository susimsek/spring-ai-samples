package io.github.susimsek.springaisamples.config;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

@RequiredArgsConstructor
public class RequestMatchersConfig {

    private final MvcRequestMatcher.Builder mvc;

    public RequestMatcher[] staticResources() {
        return new MvcRequestMatcher[] {
            mvc.pattern("/webjars/**"),
            mvc.pattern("/*.js"),
            mvc.pattern("/*.css"),
            mvc.pattern("/*.ico"),
            mvc.pattern("/*.png"),
            mvc.pattern("/*.svg"),
            mvc.pattern("/*.webapp")
        };
    }

    public String[] staticResourcePaths() {
        return new String[] {
            "/webjars/**",
            "/*.js",
            "/*.css",
            "/*.ico",
            "/*.png",
            "/*.svg",
            "/*.webapp"
        };
    }

    public RequestMatcher[] swaggerPaths() {
        return new MvcRequestMatcher[] {
            mvc.pattern("/swagger-ui.html"),
            mvc.pattern("/swagger-ui/**"),
            mvc.pattern("/v3/api-docs/**"),
            mvc.pattern("/api-docs/**"),
            mvc.pattern("/api/v1/locales")
        };
    }

    public String[] swaggerResourcePaths() {
        return new String[] {
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/api-docs/**"
        };
    }

    public String[] actuatorEndpoints() {
        return new String[] {
            "/actuator/**"
        };
    }

    public RequestMatcher[] actuatorPaths() {
        return new MvcRequestMatcher[] {
            mvc.pattern("/actuator/**")
        };
    }

    public RequestMatcher[] nonModifyingMethods() {
        return new MvcRequestMatcher[] {
            mvc.pattern(HttpMethod.GET, "/**"),
            mvc.pattern(HttpMethod.HEAD, "/**"),
            mvc.pattern(HttpMethod.OPTIONS, "/**"),
            mvc.pattern(HttpMethod.TRACE, "/**")
        };
    }

    public RequestMatcher[] encryptionPaths() {
        return new MvcRequestMatcher[] {
            mvc.pattern("/api/v{version}/security/encrypt"),
            mvc.pattern("/api/v{version}/security/decrypt")
        };
    }

    public RequestMatcher signPath() {
        return  mvc.pattern("/api/v{version}/security/sign");
    }

    public RequestMatcher tokenPath() {
        return  mvc.pattern("/api/v{version}/auth/token");
    }
}