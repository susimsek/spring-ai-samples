package io.github.susimsek.springaisamples.logging.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;

class BufferingClientHttpResponseWrapperTest {

    private ClientHttpResponse response;
    private BufferingClientHttpResponseWrapper bufferingClientHttpResponseWrapper;

    @BeforeEach
    void setUp() throws IOException {
        response = mock(ClientHttpResponse.class);
        byte[] body = "response body".getBytes();
        when(response.getBody()).thenReturn(new ByteArrayInputStream(body));
        when(response.getStatusCode()).thenReturn(HttpStatus.OK);
        when(response.getStatusText()).thenReturn("OK");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        when(response.getHeaders()).thenReturn(headers);

        bufferingClientHttpResponseWrapper = new BufferingClientHttpResponseWrapper(response);
    }

    @Test
    void testGetStatusCode() throws IOException {
        HttpStatus statusCode = bufferingClientHttpResponseWrapper.getStatusCode();
        assertEquals(HttpStatus.OK, statusCode);
    }

    @Test
    void testGetStatusText() throws IOException {
        String statusText = bufferingClientHttpResponseWrapper.getStatusText();
        assertEquals("OK", statusText);
    }

    @Test
    void testGetBody() throws IOException {
        InputStream bodyStream = bufferingClientHttpResponseWrapper.getBody();
        byte[] body = bodyStream.readAllBytes();
        assertEquals("response body", new String(body));
    }

    @Test
    void testGetHeaders() {
        HttpHeaders headers = bufferingClientHttpResponseWrapper.getHeaders();
        assertEquals("application/json", headers.getFirst("Content-Type"));
    }

    @Test
    void testClose() {
        bufferingClientHttpResponseWrapper.close();
        verify(response, times(1)).close();
    }
}