package io.github.susimsek.springaisamples.logging.interceptor;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.isNull;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.susimsek.springaisamples.logging.enums.Source;
import io.github.susimsek.springaisamples.logging.handler.LoggingHandler;
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

@ExtendWith(MockitoExtension.class)
class RestClientLoggingInterceptorTest {

    @Mock
    private LoggingHandler loggingHandler;

    @Mock
    private HttpRequest request;

    @Mock
    private ClientHttpRequestExecution execution;

    @Mock
    private ClientHttpResponse response;

    @InjectMocks
    private RestClientLoggingInterceptor interceptor;

    private byte[] requestBody;

    @BeforeEach
    void setUp() {
        requestBody = "request body".getBytes();
    }

    @Test
    void testIntercept_ShouldLogRequestAndResponse() throws IOException, URISyntaxException {
        // Arrange
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        when(request.getURI()).thenReturn(new URI("http://localhost/test"));
        when(request.getHeaders()).thenReturn(new HttpHeaders());
        when(loggingHandler.shouldNotLog(any(String.class), any(HttpMethod.class))).thenReturn(false);
        when(execution.execute(any(HttpRequest.class), any(byte[].class))).thenReturn(response);
        when(response.getBody()).thenReturn(new ByteArrayInputStream("response body".getBytes()));
        when(response.getHeaders()).thenReturn(new HttpHeaders());
        when(response.getStatusCode()).thenReturn(org.springframework.http.HttpStatus.OK);

        // Act
        interceptor.intercept(request, requestBody, execution);

        // Assert
        verify(loggingHandler, times(1)).logRequest(
            any(HttpMethod.class), any(URI.class), any(HttpHeaders.class), any(byte[].class), any(Source.class)
        );
        verify(loggingHandler, times(1)).logResponse(
            any(HttpMethod.class), any(URI.class), anyInt(), any(HttpHeaders.class), any(byte[].class), any(Source.class)
        );
    }

    @Test
    void testIntercept_ShouldNotLogWhenNotNeeded() throws IOException, URISyntaxException {
        // Arrange
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        when(request.getURI()).thenReturn(new URI("http://localhost/test"));
        when(loggingHandler.shouldNotLog(any(String.class), any(HttpMethod.class))).thenReturn(true);
        when(execution.execute(any(HttpRequest.class), any(byte[].class))).thenReturn(response);

        // Act
        interceptor.intercept(request, requestBody, execution);

        // Assert
        verify(loggingHandler, never()).logRequest(any(HttpMethod.class), any(URI.class), any(HttpHeaders.class), any(byte[].class), any(Source.class));
        verify(loggingHandler, never()).logResponse(any(HttpMethod.class), any(URI.class), anyInt(), any(HttpHeaders.class), any(byte[].class), any(Source.class));
    }

    @Test
    void testIntercept_ShouldLogErrorResponseOnIOException() throws IOException, URISyntaxException {
        // Arrange
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        when(request.getURI()).thenReturn(new URI("http://localhost/test"));
        when(request.getHeaders()).thenReturn(new HttpHeaders());
        when(loggingHandler.shouldNotLog(any(String.class), any(HttpMethod.class))).thenReturn(false);
        when(execution.execute(any(HttpRequest.class), any(byte[].class))).thenThrow(new IOException("Test IOException"));

        // Act & Assert
        IOException exception = assertThrows(IOException.class, () -> interceptor.intercept(request, requestBody, execution));

        verify(loggingHandler, times(1)).logRequest(
            any(HttpMethod.class), any(URI.class), any(HttpHeaders.class), any(byte[].class), any(Source.class)
        );
        verify(loggingHandler, times(1)).logResponse(
            any(HttpMethod.class), any(URI.class), anyInt(), any(HttpHeaders.class), isNull(), any(Source.class)
        );
        assert "Test IOException".equals(exception.getMessage());
    }

    @Test
    void testLogResponse_ShouldWrapResponse() throws IOException, URISyntaxException {
        // Arrange
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        when(request.getURI()).thenReturn(new URI("http://localhost/test"));
        when(loggingHandler.shouldNotLog(any(String.class), any(HttpMethod.class))).thenReturn(false);
        when(execution.execute(any(HttpRequest.class), any(byte[].class))).thenReturn(response);
        when(response.getBody()).thenReturn(new ByteArrayInputStream("response body".getBytes()));
        when(response.getHeaders()).thenReturn(new HttpHeaders());
        when(response.getStatusCode()).thenReturn(org.springframework.http.HttpStatus.OK);

        // Act
        ClientHttpResponse result = interceptor.intercept(request, requestBody, execution);

        // Assert
        assert result instanceof BufferingClientHttpResponseWrapper;
    }
}