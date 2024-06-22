package io.github.susimsek.springaisamples.logging.handler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.susimsek.springaisamples.logging.config.LoggingProperties;
import io.github.susimsek.springaisamples.logging.enums.LogLevel;
import io.github.susimsek.springaisamples.logging.enums.Source;
import io.github.susimsek.springaisamples.logging.formatter.LogFormatter;
import io.github.susimsek.springaisamples.logging.model.HttpLog;
import io.github.susimsek.springaisamples.logging.utils.Obfuscator;
import java.net.URI;
import java.net.URISyntaxException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

@ExtendWith(MockitoExtension.class)
class HttpLoggingHandlerTest {

    @Mock
    private LoggingProperties loggingProperties;

    @Mock
    private LogFormatter logFormatter;

    @Mock
    private Obfuscator obfuscator;

    @InjectMocks
    private HttpLoggingHandler httpLoggingHandler;

    @BeforeEach
    void setUp() {
        when(loggingProperties.getHttp()).thenReturn(new LoggingProperties.Http());
    }

    @Test
    void logRequest_shouldLogRequest_whenLogLevelIsFull() throws URISyntaxException {
        // Arrange
        URI uri = new URI("https://example.com/test");
        HttpHeaders headers = new HttpHeaders();
        byte[] body = "request body".getBytes();
        LoggingProperties.Http httpProperties = new LoggingProperties.Http();
        httpProperties.setLevel(LogLevel.FULL);

        when(loggingProperties.getHttp()).thenReturn(httpProperties);
        when(obfuscator.maskBody(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
        when(obfuscator.maskHeaders(any(HttpHeaders.class))).thenReturn(headers);
        when(logFormatter.format(any(HttpLog.class))).thenReturn("formatted log");

        // Act
        httpLoggingHandler.logRequest(HttpMethod.GET, uri, headers, body, Source.CLIENT);

        // Assert
        verify(logFormatter).format(any(HttpLog.class));
    }

    @Test
    void logResponse_shouldLogResponse_whenStatusIsClientError() throws URISyntaxException {
        // Arrange
        URI uri = new URI("https://example.com/test");
        HttpHeaders headers = new HttpHeaders();
        byte[] responseBody = "client error response".getBytes();
        int statusCode = 400;
        LoggingProperties.Http httpProperties = new LoggingProperties.Http();
        httpProperties.setLevel(LogLevel.FULL);

        when(loggingProperties.getHttp()).thenReturn(httpProperties);
        when(obfuscator.maskBody(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
        when(obfuscator.maskHeaders(any(HttpHeaders.class))).thenReturn(headers);
        when(logFormatter.format(any(HttpLog.class))).thenReturn("formatted log");

        // Act
        httpLoggingHandler.logResponse(HttpMethod.GET, uri, statusCode, headers, responseBody, Source.CLIENT);

        // Assert
        verify(logFormatter).format(any(HttpLog.class));
    }

    @Test
    void logRequest_shouldNotLog_whenShouldNotLogReturnsTrue() throws URISyntaxException {
        // Arrange
        URI uri = new URI("https://example.com/test");
        HttpHeaders headers = new HttpHeaders();
        byte[] body = "request body".getBytes();
        LoggingProperties.Http httpProperties = new LoggingProperties.Http();
        httpProperties.setLevel(LogLevel.FULL);

        when(loggingProperties.getHttp()).thenReturn(httpProperties);

        // Act
        httpLoggingHandler.logRequest(HttpMethod.GET, uri, headers, body, Source.CLIENT);

        // Assert
        verify(logFormatter, never()).format(any(HttpLog.class));
    }

    @Test
    void logResponse_shouldNotLog_whenShouldNotLogReturnsTrue() throws URISyntaxException {
        // Arrange
        URI uri = new URI("https://example.com/test");
        HttpHeaders headers = new HttpHeaders();
        byte[] responseBody = "response body".getBytes();
        int statusCode = 200;
        LoggingProperties.Http httpProperties = new LoggingProperties.Http();
        httpProperties.setLevel(LogLevel.FULL);

        when(loggingProperties.getHttp()).thenReturn(httpProperties);

        // Act
        httpLoggingHandler.logResponse(HttpMethod.GET, uri, statusCode, headers, responseBody, Source.CLIENT);

        // Assert
        verify(logFormatter, never()).format(any(HttpLog.class));
    }

    @ParameterizedTest
    @ValueSource(ints = {200, 400, 401, 403, 429, 500})
    void logResponse_shouldLogResponse_forVariousStatusCodes(int statusCode) throws URISyntaxException {
        // Arrange
        URI uri = new URI("https://example.com/test");
        HttpHeaders headers = new HttpHeaders();
        byte[] responseBody = "response body".getBytes();
        LoggingProperties.Http httpProperties = new LoggingProperties.Http();
        httpProperties.setLevel(LogLevel.FULL);

        when(loggingProperties.getHttp()).thenReturn(httpProperties);
        lenient().when(obfuscator.maskBody(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
        lenient().when(obfuscator.maskHeaders(any(HttpHeaders.class))).thenReturn(headers);
        when(logFormatter.format(any(HttpLog.class))).thenReturn("formatted log");

        // Act
        httpLoggingHandler.logResponse(HttpMethod.GET, uri, statusCode, headers, responseBody, Source.CLIENT);

        // Assert
        verify(logFormatter).format(any(HttpLog.class));
    }

    @Test
    void logResponse_shouldLogResponse_whenStatusIs429() throws URISyntaxException {
        // Arrange
        URI uri = new URI("https://example.com/test");
        HttpHeaders headers = new HttpHeaders();
        byte[] responseBody = "too many requests".getBytes();
        int statusCode = 429;
        LoggingProperties.Http httpProperties = new LoggingProperties.Http();
        httpProperties.setLevel(LogLevel.FULL);

        when(loggingProperties.getHttp()).thenReturn(httpProperties);
        when(obfuscator.maskHeaders(any(HttpHeaders.class))).thenReturn(headers);
        when(logFormatter.format(any(HttpLog.class))).thenReturn("formatted log");

        // Act
        httpLoggingHandler.logResponse(HttpMethod.GET, uri, statusCode, headers, responseBody, Source.CLIENT);

        // Assert
        verify(logFormatter).format(any(HttpLog.class));
    }

    @Test
    void logResponse_shouldLogResponse_whenStatusIs500() throws URISyntaxException {
        // Arrange
        URI uri = new URI("https://example.com/test");
        HttpHeaders headers = new HttpHeaders();
        byte[] responseBody = "internal server error".getBytes();
        int statusCode = 500;
        LoggingProperties.Http httpProperties = new LoggingProperties.Http();
        httpProperties.setLevel(LogLevel.FULL);

        when(loggingProperties.getHttp()).thenReturn(httpProperties);
        when(obfuscator.maskBody(anyString())).thenAnswer(invocation -> invocation.getArgument(0));
        when(obfuscator.maskHeaders(any(HttpHeaders.class))).thenReturn(headers);
        when(logFormatter.format(any(HttpLog.class))).thenReturn("formatted log");

        // Act
        httpLoggingHandler.logResponse(HttpMethod.GET, uri, statusCode, headers, responseBody, Source.CLIENT);

        // Assert
        verify(logFormatter).format(any(HttpLog.class));
    }

    @Test
    void logResponse_shouldLogResponse_whenLogLevelIsBasic() throws URISyntaxException {
        // Arrange
        URI uri = new URI("https://example.com/test");
        HttpHeaders headers = new HttpHeaders();
        byte[] responseBody = "basic log level response".getBytes();
        int statusCode = 200;
        LoggingProperties.Http httpProperties = new LoggingProperties.Http();
        httpProperties.setLevel(LogLevel.BASIC);

        when(loggingProperties.getHttp()).thenReturn(httpProperties);
        when(logFormatter.format(any(HttpLog.class))).thenReturn("formatted log");

        // Act
        httpLoggingHandler.logResponse(HttpMethod.GET, uri, statusCode, headers, responseBody, Source.CLIENT);

        // Assert
        verify(logFormatter).format(any(HttpLog.class));
    }

    @Test
    void logResponse_shouldLogResponse_whenLogLevelIsHeaders() throws URISyntaxException {
        // Arrange
        URI uri = new URI("https://example.com/test");
        HttpHeaders headers = new HttpHeaders();
        byte[] responseBody = "headers log level response".getBytes();
        int statusCode = 200;
        LoggingProperties.Http httpProperties = new LoggingProperties.Http();
        httpProperties.setLevel(LogLevel.HEADERS);

        when(loggingProperties.getHttp()).thenReturn(httpProperties);
        when(obfuscator.maskHeaders(any(HttpHeaders.class))).thenReturn(headers);
        when(logFormatter.format(any(HttpLog.class))).thenReturn("formatted log");

        // Act
        httpLoggingHandler.logResponse(HttpMethod.GET, uri, statusCode, headers, responseBody, Source.CLIENT);

        // Assert
        verify(logFormatter).format(any(HttpLog.class));
    }

    @Test
    void logRequest_shouldNotLog_whenLogLevelIsNone() throws URISyntaxException {
        // Arrange
        URI uri = new URI("https://example.com/test");
        HttpHeaders headers = new HttpHeaders();
        byte[] body = "request body".getBytes();
        LoggingProperties.Http httpProperties = new LoggingProperties.Http();
        httpProperties.setLevel(LogLevel.NONE);

        when(loggingProperties.getHttp()).thenReturn(httpProperties);

        // Act
        httpLoggingHandler.logRequest(HttpMethod.GET, uri, headers, body, Source.CLIENT);

        // Assert
        verify(logFormatter, never()).format(any(HttpLog.class));
    }

    @Test
    void logRequest_shouldNotLog_whenPathIsExcluded() throws URISyntaxException {
        // Arrange
        URI uri = new URI("https://example.com/test");
        HttpHeaders headers = new HttpHeaders();
        byte[] body = "request body".getBytes();
        LoggingProperties.Http httpProperties = new LoggingProperties.Http();
        httpProperties.setLevel(LogLevel.FULL);

        when(loggingProperties.getHttp()).thenReturn(httpProperties);

        // Act
        httpLoggingHandler.logRequest(HttpMethod.GET, uri, headers, body, Source.CLIENT);

        // Assert
        verify(logFormatter, never()).format(any(HttpLog.class));
    }

    @Test
    void logResponse_shouldNotLog_whenPathIsExcluded() throws URISyntaxException {
        // Arrange
        URI uri = new URI("https://example.com/test");
        HttpHeaders headers = new HttpHeaders();
        byte[] responseBody = "response body".getBytes();
        int statusCode = 200;
        LoggingProperties.Http httpProperties = new LoggingProperties.Http();
        httpProperties.setLevel(LogLevel.FULL);

        when(loggingProperties.getHttp()).thenReturn(httpProperties);

        // Act
        httpLoggingHandler.logResponse(HttpMethod.GET, uri, statusCode, headers, responseBody, Source.CLIENT);

        // Assert
        verify(logFormatter, never()).format(any(HttpLog.class));
    }

    @Test
    void logRequest_shouldNotLog_whenLogLevelIsNotHeaders() throws URISyntaxException {
        // Arrange
        URI uri = new URI("https://example.com/test");
        HttpHeaders headers = new HttpHeaders();
        byte[] body = "request body".getBytes();
        LoggingProperties.Http httpProperties = new LoggingProperties.Http();
        httpProperties.setLevel(LogLevel.BASIC); // Not HEADERS level

        when(loggingProperties.getHttp()).thenReturn(httpProperties);

        // Act
        httpLoggingHandler.logRequest(HttpMethod.GET, uri, headers, body, Source.CLIENT);

        // Assert
        verify(obfuscator, never()).maskHeaders(any(HttpHeaders.class));
        verify(logFormatter).format(any(HttpLog.class));
    }
}