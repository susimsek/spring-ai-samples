package io.github.susimsek.springaisamples.logging.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import io.github.susimsek.springaisamples.logging.enums.HttpLogType;
import io.github.susimsek.springaisamples.logging.enums.Source;
import java.net.URI;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

class HttpLogTest {

    @Test
    void testBuilder() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        HttpLog httpLog = HttpLog.builder()
            .type(HttpLogType.REQUEST)
            .method(HttpMethod.GET)
            .uri(URI.create("http://localhost"))
            .statusCode(200)
            .headers(headers)
            .body("{\"key\":\"value\"}")
            .build();

        assertEquals(HttpLogType.REQUEST, httpLog.getType());
        assertEquals(HttpMethod.GET, httpLog.getMethod());
        assertEquals(URI.create("http://localhost"), httpLog.getUri());
        assertEquals(200, httpLog.getStatusCode());
        assertEquals(headers, httpLog.getHeaders());
        assertEquals("{\"key\":\"value\"}", httpLog.getBody());
    }

    @Test
    void testSetterAndGetter() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        HttpLog httpLog = new HttpLog();
        httpLog.setType(HttpLogType.RESPONSE);
        httpLog.setMethod(HttpMethod.POST);
        httpLog.setUri(URI.create("http://localhost"));
        httpLog.setStatusCode(201);
        httpLog.setHeaders(headers);
        httpLog.setBody("plain text body");
        httpLog.setSource(Source.CLIENT);

        assertEquals(HttpLogType.RESPONSE, httpLog.getType());
        assertEquals(Source.CLIENT, httpLog.getSource());
        assertEquals("POST", httpLog.getMethod());
        assertEquals(URI.create("http://localhost"), httpLog.getUri());
        assertEquals(201, httpLog.getStatusCode());
        assertEquals(headers, httpLog.getHeaders());
        assertEquals("plain text body", httpLog.getBody());
    }

    @Test
    void testDefaultValues() {
        HttpLog httpLog = new HttpLog();

        assertNull(httpLog.getType());
        assertNull(httpLog.getMethod());
        assertNull(httpLog.getUri());
        assertNull(httpLog.getStatusCode());
        assertNull(httpLog.getHeaders());
        assertNull(httpLog.getBody());
        assertNull(httpLog.getSource());
    }
}