package io.github.susimsek.springaisamples.security;

import io.github.susimsek.springaisamples.logging.utils.CachedBodyHttpServletRequestWrapper;
import io.github.susimsek.springaisamples.utils.SanitizationUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.stream.Stream;

public class XssRequestWrapper extends CachedBodyHttpServletRequestWrapper {

    private final SanitizationUtil sanitizationUtil;

    public XssRequestWrapper(HttpServletRequest request, SanitizationUtil sanitizationUtil) throws IOException {
        super(request);
        this.sanitizationUtil = sanitizationUtil;
        sanitizeBody();
    }

    private void sanitizeBody() {
        byte[] body = getBody();
        String bodyString = new String(body, StandardCharsets.UTF_8);
        String sanitizedBodyString = sanitizationUtil.sanitizeJsonString(bodyString);
        byte[] sanitizedBody = sanitizedBodyString.getBytes(StandardCharsets.UTF_8);
        setBody(sanitizedBody);
    }

    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        return value != null ? sanitizationUtil.sanitizeInput(value) : null;
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values == null) {
            return new String[0];
        }
        return Stream.of(values)
                     .map(sanitizationUtil::sanitizeInput)
                     .toArray(String[]::new);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map<String, String[]> parameterMap = super.getParameterMap();
        parameterMap.replaceAll((key, values) ->
            Stream.of(values)
                  .map(sanitizationUtil::sanitizeInput)
                  .toArray(String[]::new)
        );
        return parameterMap;
    }

    @Override
    public String getHeader(String name) {
        String value = super.getHeader(name);
        return value != null ? sanitizationUtil.sanitizeInput(value) : null;
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        return Collections.enumeration(
            Collections.list(super.getHeaders(name)).stream()
                .map(sanitizationUtil::sanitizeInput)
                .toList()
        );
    }


    @Override
    public Enumeration<String> getHeaderNames() {
        return Collections.enumeration(
            Collections.list(super.getHeaderNames()).stream()
                .map(sanitizationUtil::sanitizeInput)
                .toList()
        );
    }

    @Override
    public String getQueryString() {
        String queryString = super.getQueryString();
        return queryString != null ? sanitizationUtil.sanitizeInput(queryString) : null;
    }

    @Override
    public String getRequestURI() {
        String uri = super.getRequestURI();
        return uri != null ? sanitizationUtil.sanitizeInput(uri) : null;
    }

    @Override
    public StringBuffer getRequestURL() {
        StringBuffer requestURL = super.getRequestURL();
        return requestURL != null ? new StringBuffer(sanitizationUtil.sanitizeInput(requestURL.toString())) : null;
    }

    @Override
    public String getServletPath() {
        String servletPath = super.getServletPath();
        return servletPath != null ? sanitizationUtil.sanitizeInput(servletPath) : null;
    }

    @Override
    public String getPathInfo() {
        String pathInfo = super.getPathInfo();
        return pathInfo != null ? sanitizationUtil.sanitizeInput(pathInfo) : null;
    }
}