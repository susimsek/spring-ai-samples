package io.github.susimsek.springaisamples.logging.handler;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import io.github.susimsek.springaisamples.logging.config.LoggingProperties;
import io.github.susimsek.springaisamples.logging.enums.LogLevel;
import io.github.susimsek.springaisamples.logging.formatter.LogFormatter;
import io.github.susimsek.springaisamples.logging.model.HttpLog;
import io.github.susimsek.springaisamples.logging.utils.Obfuscator;
import io.github.susimsek.springaisamples.logging.utils.PathFilter;
import java.net.URI;
import java.net.URISyntaxException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;

@ExtendWith(MockitoExtension.class)
class HttpLoggingHandlerTest {

    @Mock
    private LoggingProperties loggingProperties;

    @Mock
    private LogFormatter logFormatter;

    @Mock
    private Obfuscator obfuscator;

    @Mock
    private PathFilter pathFilter;

    @InjectMocks
    private HttpLoggingHandler httpLoggingHandler;

    @BeforeEach
    void setUp() {
        LoggingProperties.Http httpProperties = new LoggingProperties.Http();
        when(loggingProperties.getHttp()).thenReturn(httpProperties);
    }

    @Test
    void logRequest_ShouldLogWhenLogLevelIsNotNone() throws URISyntaxException {
        LoggingProperties.Http httpProperties = loggingProperties.getHttp();
        httpProperties.setLevel(LogLevel.FULL);

        URI uri = new URI("http://example.com");
        HttpHeaders headers = new HttpHeaders();
        byte[] body = "request body".getBytes();

        when(pathFilter.shouldInclude(anyString(), anyString())).thenReturn(true);
        when(pathFilter.shouldExclude(anyString(), anyString())).thenReturn(false);
        when(obfuscator.maskHeaders(headers)).thenReturn(headers);
        when(obfuscator.maskBody(anyString())).thenReturn("masked body");
        when(logFormatter.format(any(HttpLog.class))).thenReturn("formatted log");

        httpLoggingHandler.logRequest("GET", uri, headers, body);

        ArgumentCaptor<HttpLog> logCaptor = ArgumentCaptor.forClass(HttpLog.class);
        verify(logFormatter).format(logCaptor.capture());
        assertEquals("formatted log", logFormatter.format(logCaptor.getValue()));
    }

    @Test
    void logResponse_ShouldLogWhenLogLevelIsNotNone() throws URISyntaxException {
        LoggingProperties.Http httpProperties = loggingProperties.getHttp();
        httpProperties.setLevel(LogLevel.FULL);

        URI uri = new URI("http://example.com");
        HttpHeaders headers = new HttpHeaders();
        byte[] responseBody = "response body".getBytes();

        when(pathFilter.shouldInclude(anyString(), anyString())).thenReturn(true);
        when(pathFilter.shouldExclude(anyString(), anyString())).thenReturn(false);
        when(obfuscator.maskHeaders(headers)).thenReturn(headers);
        when(obfuscator.maskBody(anyString())).thenReturn("masked body");
        when(logFormatter.format(any(HttpLog.class))).thenReturn("formatted log");

        httpLoggingHandler.logResponse("GET", uri, 200, headers, responseBody);

        ArgumentCaptor<HttpLog> logCaptor = ArgumentCaptor.forClass(HttpLog.class);
        verify(logFormatter).format(logCaptor.capture());
        assertEquals("formatted log", logFormatter.format(logCaptor.getValue()));
    }

    @Test
    void logRequest_ShouldNotLogWhenLogLevelIsNone() throws URISyntaxException {
        LoggingProperties.Http httpProperties = loggingProperties.getHttp();
        httpProperties.setLevel(LogLevel.NONE);

        URI uri = new URI("http://example.com");
        HttpHeaders headers = new HttpHeaders();
        byte[] body = "request body".getBytes();

        httpLoggingHandler.logRequest("GET", uri, headers, body);

        verify(logFormatter, never()).format(any());
    }

    @Test
    void logResponse_ShouldNotLogWhenLogLevelIsNone() throws URISyntaxException {
        LoggingProperties.Http httpProperties = loggingProperties.getHttp();
        httpProperties.setLevel(LogLevel.NONE);

        URI uri = new URI("http://example.com");
        HttpHeaders headers = new HttpHeaders();
        byte[] responseBody = "response body".getBytes();

        httpLoggingHandler.logResponse("GET", uri, 200, headers, responseBody);

        verify(logFormatter, never()).format(any());
    }

    @Test
    void logRequest_ShouldLogHeadersWhenLogLevelIsHeaders() throws URISyntaxException {
        LoggingProperties.Http httpProperties = loggingProperties.getHttp();
        httpProperties.setLevel(LogLevel.HEADERS);

        URI uri = new URI("http://example.com");
        HttpHeaders headers = new HttpHeaders();
        byte[] body = "request body".getBytes();

        when(pathFilter.shouldInclude(anyString(), anyString())).thenReturn(true);
        when(pathFilter.shouldExclude(anyString(), anyString())).thenReturn(false);
        when(obfuscator.maskHeaders(headers)).thenReturn(headers);
        when(logFormatter.format(any())).thenReturn("formatted log");

        httpLoggingHandler.logRequest("GET", uri, headers, body);

        ArgumentCaptor<HttpLog> logCaptor = ArgumentCaptor.forClass(HttpLog.class);
        verify(logFormatter).format(logCaptor.capture());
        HttpLog capturedLog = logCaptor.getValue();
        assertEquals(headers, capturedLog.getHeaders());
        assertNull(capturedLog.getBody());  // Body should not be logged in HEADERS level
    }

    @Test
    void logResponse_ShouldLogHeadersWhenLogLevelIsHeaders() throws URISyntaxException {
        LoggingProperties.Http httpProperties = loggingProperties.getHttp();
        httpProperties.setLevel(LogLevel.HEADERS);

        URI uri = new URI("http://example.com");
        HttpHeaders headers = new HttpHeaders();
        byte[] responseBody = "response body".getBytes();

        when(pathFilter.shouldInclude(anyString(), anyString())).thenReturn(true);
        when(pathFilter.shouldExclude(anyString(), anyString())).thenReturn(false);
        when(obfuscator.maskHeaders(headers)).thenReturn(headers);
        when(logFormatter.format(any())).thenReturn("formatted log");

        httpLoggingHandler.logResponse("GET", uri, 200, headers, responseBody);

        ArgumentCaptor<HttpLog> logCaptor = ArgumentCaptor.forClass(HttpLog.class);
        verify(logFormatter).format(logCaptor.capture());
        HttpLog capturedLog = logCaptor.getValue();
        assertEquals(headers, capturedLog.getHeaders());
        assertNull(capturedLog.getBody());  // Body should not be logged in HEADERS level
    }

    @Test
    void logRequest_ShouldNotLogWhenPathShouldBeExcluded() throws URISyntaxException {
        LoggingProperties.Http httpProperties = loggingProperties.getHttp();
        httpProperties.setLevel(LogLevel.FULL);

        URI uri = new URI("http://example.com");
        HttpHeaders headers = new HttpHeaders();
        byte[] body = "request body".getBytes();

        when(pathFilter.shouldInclude(anyString(), anyString())).thenReturn(true);
        when(pathFilter.shouldExclude(anyString(), anyString())).thenReturn(true);

        httpLoggingHandler.logRequest("GET", uri, headers, body);

        verify(logFormatter, never()).format(any());
    }

    @Test
    void logResponse_ShouldNotLogWhenPathShouldBeExcluded() throws URISyntaxException {
        LoggingProperties.Http httpProperties = loggingProperties.getHttp();
        httpProperties.setLevel(LogLevel.FULL);

        URI uri = new URI("http://example.com");
        HttpHeaders headers = new HttpHeaders();
        byte[] responseBody = "response body".getBytes();

        when(pathFilter.shouldInclude(anyString(), anyString())).thenReturn(true);
        when(pathFilter.shouldExclude(anyString(), anyString())).thenReturn(true);

        httpLoggingHandler.logResponse("GET", uri, 200, headers, responseBody);

        verify(logFormatter, never()).format(any());
    }

    @Test
    void logRequest_ShouldLogWhenLogLevelIsBasic() throws URISyntaxException {
        LoggingProperties.Http httpProperties = loggingProperties.getHttp();
        httpProperties.setLevel(LogLevel.BASIC);

        URI uri = new URI("http://example.com");
        HttpHeaders headers = new HttpHeaders();
        byte[] body = "request body".getBytes();

        when(pathFilter.shouldInclude(anyString(), anyString())).thenReturn(true);
        when(pathFilter.shouldExclude(anyString(), anyString())).thenReturn(false);
        when(logFormatter.format(any())).thenReturn("formatted log");

        httpLoggingHandler.logRequest("GET", uri, headers, body);

        ArgumentCaptor<HttpLog> logCaptor = ArgumentCaptor.forClass(HttpLog.class);
        verify(logFormatter).format(logCaptor.capture());
        HttpLog capturedLog = logCaptor.getValue();
        assertEquals(headers, capturedLog.getHeaders());
        assertNull(capturedLog.getBody());  // Body should not be logged in BASIC level
    }

    @Test
    void logResponse_ShouldLogWhenLogLevelIsBasic() throws URISyntaxException {
        LoggingProperties.Http httpProperties = loggingProperties.getHttp();
        httpProperties.setLevel(LogLevel.BASIC);

        URI uri = new URI("http://example.com");
        HttpHeaders headers = new HttpHeaders();
        byte[] responseBody = "response body".getBytes();

        when(pathFilter.shouldInclude(anyString(), anyString())).thenReturn(true);
        when(pathFilter.shouldExclude(anyString(), anyString())).thenReturn(false);
        when(logFormatter.format(any())).thenReturn("formatted log");

        httpLoggingHandler.logResponse("GET", uri, 200, headers, responseBody);

        ArgumentCaptor<HttpLog> logCaptor = ArgumentCaptor.forClass(HttpLog.class);
        verify(logFormatter).format(logCaptor.capture());
        HttpLog capturedLog = logCaptor.getValue();
        assertEquals(headers, capturedLog.getHeaders());
        assertNull(capturedLog.getBody());  // Body should not be logged in BASIC level
    }

    @Test
    void logRequest_ShouldNotLogWhenShouldIncludeIsFalse() throws URISyntaxException {
        LoggingProperties.Http httpProperties = loggingProperties.getHttp();
        httpProperties.setLevel(LogLevel.FULL);

        URI uri = new URI("http://example.com");
        HttpHeaders headers = new HttpHeaders();
        byte[] body = "request body".getBytes();

        when(pathFilter.shouldInclude(anyString(), anyString())).thenReturn(false);

        httpLoggingHandler.logRequest("GET", uri, headers, body);

        verify(logFormatter, never()).format(any());
    }

    @Test
    void logResponse_ShouldNotLogWhenShouldIncludeIsFalse() throws URISyntaxException {
        LoggingProperties.Http httpProperties = loggingProperties.getHttp();
        httpProperties.setLevel(LogLevel.FULL);

        URI uri = new URI("http://example.com");
        HttpHeaders headers = new HttpHeaders();
        byte[] responseBody = "response body".getBytes();

        when(pathFilter.shouldInclude(anyString(), anyString())).thenReturn(false);

        httpLoggingHandler.logResponse("GET", uri, 200, headers, responseBody);

        verify(logFormatter, never()).format(any());
    }

    @Test
    void logRequest_ShouldNotLogWhenFormattedLogIsNull() throws URISyntaxException {
        LoggingProperties.Http httpProperties = loggingProperties.getHttp();
        httpProperties.setLevel(LogLevel.FULL);

        URI uri = new URI("http://example.com");
        HttpHeaders headers = new HttpHeaders();
        byte[] body = "request body".getBytes();

        when(pathFilter.shouldInclude(anyString(), anyString())).thenReturn(true);
        when(pathFilter.shouldExclude(anyString(), anyString())).thenReturn(false);
        when(obfuscator.maskHeaders(headers)).thenReturn(headers);
        when(obfuscator.maskBody(anyString())).thenReturn("masked body");
        when(logFormatter.format(any())).thenReturn(null);  // Simulate null formatted log

        httpLoggingHandler.logRequest("GET", uri, headers, body);

        verify(logFormatter).format(any());
        verifyNoMoreInteractions(logFormatter);  // Ensure no log output occurs
    }

    @Test
    void logResponse_ShouldNotLogWhenFormattedLogIsNull() throws URISyntaxException {
        LoggingProperties.Http httpProperties = loggingProperties.getHttp();
        httpProperties.setLevel(LogLevel.FULL);

        URI uri = new URI("http://example.com");
        HttpHeaders headers = new HttpHeaders();
        byte[] responseBody = "response body".getBytes();

        when(pathFilter.shouldInclude(anyString(), anyString())).thenReturn(true);
        when(pathFilter.shouldExclude(anyString(), anyString())).thenReturn(false);
        when(obfuscator.maskHeaders(headers)).thenReturn(headers);
        when(obfuscator.maskBody(anyString())).thenReturn("masked body");
        when(logFormatter.format(any())).thenReturn(null);  // Simulate null formatted log

        httpLoggingHandler.logResponse("GET", uri, 200, headers, responseBody);

        verify(logFormatter).format(any());
        verifyNoMoreInteractions(logFormatter);  // Ensure no log output occurs
    }
}