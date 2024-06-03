package io.github.susimsek.springaisamples.logging.interceptor;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.susimsek.springaisamples.logging.handler.HttpLoggingHandler;
import io.github.susimsek.springaisamples.logging.utils.BufferingClientHttpResponseWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.mock.http.client.MockClientHttpResponse;

@ExtendWith(MockitoExtension.class)
class RestClientLoggingInterceptorTest {

    @Mock
    private HttpLoggingHandler httpLoggingHandler;

    @Mock
    private ClientHttpRequestExecution execution;

    @InjectMocks
    private RestClientLoggingInterceptor interceptor;

    private HttpRequest request;
    private byte[] body;

    @BeforeEach
    void setUp() throws Exception {
        request = mock(HttpRequest.class);
        body = "request-body".getBytes();

        when(request.getMethod()).thenReturn(HttpMethod.GET);
        when(request.getURI()).thenReturn(new URI("http://example.com"));
        when(request.getHeaders()).thenReturn(new HttpHeaders());
    }

    @Test
    void intercept_shouldLogRequestAndResponse() throws IOException, URISyntaxException {
        // Given
        MockClientHttpResponse mockResponse = new MockClientHttpResponse(new ByteArrayInputStream("response-body".getBytes()), 200);
        when(execution.execute(any(HttpRequest.class), any(byte[].class))).thenReturn(mockResponse);

        // When
        ClientHttpResponse response = interceptor.intercept(request, body, execution);

        // Then
        verify(httpLoggingHandler).logRequest(eq("GET"), eq(new URI("http://example.com")), any(HttpHeaders.class), eq(body));
        verify(httpLoggingHandler).logResponse(eq("GET"), eq(new URI("http://example.com")), eq(200), any(HttpHeaders.class), eq("response-body".getBytes()));
        assertEquals(200, response.getStatusCode().value());
        assertArrayEquals("response-body".getBytes(), ((BufferingClientHttpResponseWrapper) response).getBody().readAllBytes());
    }
}