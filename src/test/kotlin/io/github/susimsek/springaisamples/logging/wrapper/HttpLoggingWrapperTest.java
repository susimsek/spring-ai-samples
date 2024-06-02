package io.github.susimsek.springaisamples.logging.wrapper;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.susimsek.springaisamples.logging.handler.HttpLoggingHandler;
import io.github.susimsek.springaisamples.logging.utils.BufferingClientHttpResponseWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

@ExtendWith(MockitoExtension.class)
class HttpLoggingWrapperTest {

    @Mock
    private HttpLoggingHandler httpLoggingHandler;

    @InjectMocks
    private HttpLoggingWrapper httpLoggingWrapper;

    @Mock
    private HttpRequest httpRequest;

    @Mock
    private ClientHttpRequestExecution clientHttpRequestExecution;

    @Mock
    private ClientHttpResponse clientHttpResponse;

    @Test
    void createRestClientInterceptor() throws IOException, URISyntaxException {
        byte[] requestBody = "request body".getBytes();
        byte[] responseBody = "response body".getBytes();

        HttpHeaders httpHeaders = new HttpHeaders();

        when(httpRequest.getMethod()).thenReturn(HttpMethod.GET);
        when(httpRequest.getURI()).thenReturn(new URI("http://example.com"));
        when(httpRequest.getHeaders()).thenReturn(httpHeaders);
        when(clientHttpResponse.getStatusCode()).thenReturn(org.springframework.http.HttpStatus.OK);
        when(clientHttpResponse.getBody()).thenReturn(new ByteArrayInputStream(responseBody));
        when(clientHttpRequestExecution.execute(httpRequest, requestBody)).thenReturn(clientHttpResponse);

        ClientHttpRequestInterceptor interceptor = httpLoggingWrapper.createRestClientInterceptor();

        try (ClientHttpResponse response = interceptor.intercept(httpRequest, requestBody, clientHttpRequestExecution)) {
            // Check the type and content of the response
            assertEquals(BufferingClientHttpResponseWrapper.class, response.getClass());
            assertEquals(clientHttpResponse.getStatusCode(), response.getStatusCode());
            assertEquals(clientHttpResponse.getStatusText(), response.getStatusText());
            assertArrayEquals(responseBody, response.getBody().readAllBytes());
        }

        ArgumentCaptor<String> methodCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<URI> uriCaptor = ArgumentCaptor.forClass(URI.class);
        ArgumentCaptor<HttpHeaders> headersCaptor = ArgumentCaptor.forClass(HttpHeaders.class);
        ArgumentCaptor<byte[]> bodyCaptor = ArgumentCaptor.forClass(byte[].class);

        verify(httpLoggingHandler).logRequest(methodCaptor.capture(), uriCaptor.capture(), headersCaptor.capture(), bodyCaptor.capture());
        assertEquals("GET", methodCaptor.getValue());
        assertEquals(new URI("http://example.com"), uriCaptor.getValue());
        assertEquals(httpHeaders, headersCaptor.getValue());
        assertArrayEquals(requestBody, bodyCaptor.getValue());

        verify(httpLoggingHandler).logResponse(methodCaptor.capture(), uriCaptor.capture(), anyInt(), headersCaptor.capture(), bodyCaptor.capture());
        assertEquals("GET", methodCaptor.getValue());
        assertEquals(new URI("http://example.com"), uriCaptor.getValue());
        assertArrayEquals(responseBody, bodyCaptor.getValue());
    }
}