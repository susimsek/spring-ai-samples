package io.github.susimsek.springaisamples.security.xss;

import io.github.susimsek.springaisamples.utils.CachedBodyHttpServletRequestWrapper;
import io.github.susimsek.springaisamples.utils.SanitizationUtil;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class XssRequestWrapper extends CachedBodyHttpServletRequestWrapper {

    private final SanitizationUtil sanitizationUtil;
    private final List<String> nonSanitizedHeaders;

    public XssRequestWrapper(HttpServletRequest request, SanitizationUtil sanitizationUtil,
                             List<String> nonSanitizedHeaders) throws IOException {
        super(request);
        this.sanitizationUtil = sanitizationUtil;
        this.nonSanitizedHeaders = nonSanitizedHeaders;
        sanitizeBody();
    }

    private void sanitizeBody() {
        String body = getContentAsString();
        String sanitizedBodyString = sanitizationUtil.sanitizeJsonString(body);
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
        if (isNonSanitized(name)) {
            return super.getHeader(name);
        }
        String value = super.getHeader(name);
        return value != null ? sanitizationUtil.sanitizeInput(value) : null;
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        if (isNonSanitized(name)) {
            return super.getHeaders(name);
        }
        return Collections.enumeration(
            Collections.list(super.getHeaders(name)).stream()
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

    private boolean isNonSanitized(String headerName) {
        return nonSanitizedHeaders.stream()
            .anyMatch(item -> item.equalsIgnoreCase(headerName));
    }
}
