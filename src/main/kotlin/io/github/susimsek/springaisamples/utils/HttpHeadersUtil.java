package io.github.susimsek.springaisamples.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpHeaders;

@UtilityClass
public class HttpHeadersUtil {

    public void setHttpHeadersToResponse(HttpHeaders httpHeaders, HttpServletResponse response) {
        httpHeaders.forEach((headerName, headerValues) ->
            headerValues.forEach(headerValue ->
                response.addHeader(headerName, headerValue)
            )
        );
    }

    public HttpHeaders convertToHttpHeaders(HttpServletResponse response) {
        HttpHeaders httpHeaders = new HttpHeaders();

        response.getHeaderNames().forEach(headerName ->
            httpHeaders.addAll(headerName, List.copyOf(response.getHeaders(headerName)))
        );

        return httpHeaders;
    }

    public HttpHeaders convertToHttpHeaders(HttpServletRequest request) {
        HttpHeaders httpHeaders = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            Enumeration<String> headers = request.getHeaders(headerName);
            httpHeaders.addAll(headerName, Collections.list(headers));
        }

        return httpHeaders;
    }
}