package io.github.susimsek.springaisamples.logging.interceptor;

/*
class RestClientLoggingInterceptorTest {

    private LoggingProperties loggingProperties;
    private LogFormatter logFormatter;
    private Obfuscator obfuscator;
    private RestClientLoggingInterceptor interceptor;

    @BeforeEach
    void setUp() {
        loggingProperties = mock(LoggingProperties.class);
        logFormatter = mock(LogFormatter.class);
        obfuscator = mock(Obfuscator.class);
        interceptor = new RestClientLoggingInterceptor(loggingProperties, logFormatter, obfuscator);
    }

    @Test
    void testInterceptWithLogLevelNone() throws IOException {
        HttpRequest request = mock(HttpRequest.class);
        byte[] body = "request body".getBytes();
        ClientHttpRequestExecution execution = mock(ClientHttpRequestExecution.class);
        ClientHttpResponse response = mock(ClientHttpResponse.class);

        when(loggingProperties.getLevel()).thenReturn(LogLevel.NONE);
        when(execution.execute(request, body)).thenReturn(response);

        ClientHttpResponse result = interceptor.intercept(request, body, execution);

        verify(execution, times(1)).execute(request, body);
        verifyNoMoreInteractions(logFormatter, obfuscator);
        assertNotNull(result);
    }

    @Test
    void testInterceptWithLogLevelFull() throws IOException {
        HttpRequest request = mock(HttpRequest.class);
        byte[] body = "request body".getBytes();
        ClientHttpRequestExecution execution = mock(ClientHttpRequestExecution.class);
        ClientHttpResponse response = mock(ClientHttpResponse.class);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        when(loggingProperties.getLevel()).thenReturn(LogLevel.FULL);
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        when(request.getURI()).thenReturn(URI.create("http://localhost"));
        when(response.getStatusCode()).thenReturn(org.springframework.http.HttpStatus.OK);
        when(response.getHeaders()).thenReturn(headers);
        when(response.getBody()).thenReturn(new ByteArrayInputStream("response body".getBytes()));
        when(execution.execute(request, body)).thenReturn(response);
        when(obfuscator.maskUriParameters(any(URI.class))).thenReturn(URI.create("http://localhost"));
        when(obfuscator.maskHeaders(any(HttpHeaders.class))).thenReturn(headers);
        when(obfuscator.maskBody(any(String.class))).thenReturn("masked body");

        ClientHttpResponse result = interceptor.intercept(request, body, execution);

        verify(execution, times(1)).execute(request, body);
        verify(obfuscator, times(1)).maskUriParameters(any(URI.class));
        verify(obfuscator, times(1)).maskHeaders(any(HttpHeaders.class));
        verify(obfuscator, times(2)).maskBody(any(String.class));
        verify(logFormatter, times(2)).format(any(HttpLog.class));
        assertNotNull(result);
    }

    @Test
    void testInterceptWithException() throws IOException {
        HttpRequest request = mock(HttpRequest.class);
        byte[] body = "request body".getBytes();
        ClientHttpRequestExecution execution = mock(ClientHttpRequestExecution.class);
        IOException exception = new IOException("Test exception");
        HttpHeaders headers = new HttpHeaders();

        when(loggingProperties.getLevel()).thenReturn(LogLevel.FULL);
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        when(request.getURI()).thenReturn(URI.create("http://localhost"));
        when(request.getHeaders()).thenReturn(headers);
        when(obfuscator.maskUriParameters(any(URI.class))).thenReturn(URI.create("http://localhost"));
        when(obfuscator.maskHeaders(any(HttpHeaders.class))).thenReturn(headers);
        when(obfuscator.maskBody(any(String.class))).thenReturn("masked body");

        doThrow(exception).when(execution).execute(request, body);

        assertThrows(IOException.class, () -> interceptor.intercept(request, body, execution));

        verify(execution, times(1)).execute(request, body);
        verify(obfuscator, times(1)).maskUriParameters(any(URI.class));
        verify(logFormatter, times(1)).format(any(HttpLog.class));
    }

    @Test
    void testLogResponseWithIOException() throws IOException {
        HttpRequest request = mock(HttpRequest.class);
        ClientHttpResponse response = mock(ClientHttpResponse.class);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        when(loggingProperties.getLevel()).thenReturn(LogLevel.FULL);
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        when(request.getURI()).thenReturn(URI.create("http://localhost"));
        when(request.getHeaders()).thenReturn(headers);
        when(response.getStatusCode()).thenThrow(new IOException("Test IOException"));
        when(response.getHeaders()).thenReturn(headers);
        when(response.getBody()).thenReturn(new ByteArrayInputStream("response body".getBytes()));
        when(obfuscator.maskUriParameters(any(URI.class))).thenReturn(URI.create("http://localhost"));
        when(obfuscator.maskHeaders(any(HttpHeaders.class))).thenReturn(headers);
        when(obfuscator.maskBody(any(String.class))).thenReturn("masked body");

        assertThrows(IOException.class, () -> interceptor.intercept(
            request,
            "request body".getBytes(),
            (req, bod) -> response));

        verify(obfuscator, times(1)).maskUriParameters(any(URI.class));
        verify(obfuscator, times(1)).maskHeaders(any(HttpHeaders.class));
        verify(obfuscator, times(1)).maskBody(any(String.class));
        verify(logFormatter, times(1)).format(any(HttpLog.class));
    }

    @Test
    void testInterceptWithLogLevelBasic() throws IOException {
        HttpRequest request = mock(HttpRequest.class);
        byte[] body = "request body".getBytes();
        ClientHttpRequestExecution execution = mock(ClientHttpRequestExecution.class);
        ClientHttpResponse response = mock(ClientHttpResponse.class);

        when(loggingProperties.getLevel()).thenReturn(LogLevel.BASIC);
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        when(request.getURI()).thenReturn(URI.create("http://localhost"));
        when(response.getStatusCode()).thenReturn(org.springframework.http.HttpStatus.OK);
        when(response.getBody()).thenReturn(new ByteArrayInputStream("response body".getBytes()));
        when(execution.execute(request, body)).thenReturn(response);
        when(obfuscator.maskUriParameters(any(URI.class))).thenReturn(URI.create("http://localhost"));

        ClientHttpResponse result = interceptor.intercept(request, body, execution);

        verify(execution, times(1)).execute(request, body);
        verify(obfuscator, times(1)).maskUriParameters(any(URI.class));
        verify(logFormatter, times(2)).format(any(HttpLog.class));
        assertNotNull(result);
    }

    @Test
    void testInterceptWithLogLevelHeaders() throws IOException {
        HttpRequest request = mock(HttpRequest.class);
        byte[] body = "request body".getBytes();
        ClientHttpRequestExecution execution = mock(ClientHttpRequestExecution.class);
        ClientHttpResponse response = mock(ClientHttpResponse.class);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");

        when(loggingProperties.getLevel()).thenReturn(LogLevel.HEADERS);
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        when(request.getURI()).thenReturn(URI.create("http://localhost"));
        when(response.getStatusCode()).thenReturn(org.springframework.http.HttpStatus.OK);
        when(response.getHeaders()).thenReturn(headers);
        when(response.getBody()).thenReturn(new ByteArrayInputStream("response body".getBytes()));
        when(execution.execute(request, body)).thenReturn(response);
        when(obfuscator.maskUriParameters(any(URI.class))).thenReturn(URI.create("http://localhost"));
        when(obfuscator.maskHeaders(any(HttpHeaders.class))).thenReturn(headers);

        ClientHttpResponse result = interceptor.intercept(request, body, execution);

        verify(execution, times(1)).execute(request, body);
        verify(obfuscator, times(1)).maskUriParameters(any(URI.class));
        verify(obfuscator, times(1)).maskHeaders(any(HttpHeaders.class));
        verify(logFormatter, times(2)).format(any(HttpLog.class));
        assertNotNull(result);
    }
}

 */