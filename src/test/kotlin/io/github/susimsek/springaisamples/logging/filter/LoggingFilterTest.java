package io.github.susimsek.springaisamples.logging.filter;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.susimsek.springaisamples.logging.enums.Source;
import io.github.susimsek.springaisamples.logging.handler.LoggingHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.Enumeration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

@ExtendWith(MockitoExtension.class)
class LoggingFilterTest {

    @Mock
    private LoggingHandler loggingHandler;

    @Mock
    private FilterChain filterChain;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private LoggingFilter loggingFilter;

    @Captor
    private ArgumentCaptor<byte[]> requestContentCaptor;

    @Captor
    private ArgumentCaptor<byte[]> responseContentCaptor;

    @Test
    void testDoFilterInternal_ShouldLog() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/test");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost/test"));
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeaderNames()).thenReturn(Collections.enumeration(Collections.emptyList()));
        when(response.getHeaderNames()).thenReturn(Collections.emptyList());
        when(loggingHandler.shouldNotLog("/test", "GET")).thenReturn(false);

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        // Act
        loggingFilter.doFilterInternal(wrappedRequest, wrappedResponse, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(any(ContentCachingRequestWrapper.class),
            any(ContentCachingResponseWrapper.class));
        verify(loggingHandler, times(1)).logRequest(
            anyString(),
            any(URI.class),
            any(HttpHeaders.class),
            requestContentCaptor.capture(),
            any(Source.class)
        );
        verify(loggingHandler, times(1)).logResponse(
            anyString(),
            any(URI.class),
            anyInt(), // default status code for wrappedResponse
            any(HttpHeaders.class),
            responseContentCaptor.capture(),
            any(Source.class)
        );

        assertArrayEquals(wrappedRequest.getContentAsByteArray(), requestContentCaptor.getValue());
        assertArrayEquals(wrappedResponse.getContentAsByteArray(), responseContentCaptor.getValue());
    }

    @Test
    void testDoFilterInternal_ShouldNotLog() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/test");
        when(request.getMethod()).thenReturn("GET");
        when(loggingHandler.shouldNotLog("/test", "GET")).thenReturn(true);

        // Act
        loggingFilter.doFilterInternal(request, response, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(any(HttpServletRequest.class), any(HttpServletResponse.class));
        verify(loggingHandler, never()).logRequest(any(String.class), any(URI.class), any(HttpHeaders.class),
            any(byte[].class), any(Source.class));
        verify(loggingHandler, never()).logResponse(any(String.class), any(URI.class), anyInt(), any(HttpHeaders.class),
            any(byte[].class), any(Source.class));
    }

    @Test
    void testLogRequestAndResponse_InvalidUriSyntax() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/test");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8080/invalid uri"));
        when(request.getMethod()).thenReturn("GET");
        when(loggingHandler.shouldNotLog(anyString(), anyString())).thenReturn(false);

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        // Act
        loggingFilter.doFilterInternal(wrappedRequest, wrappedResponse, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(any(ContentCachingRequestWrapper.class),
            any(ContentCachingResponseWrapper.class));
    }

    @Test
    void testDoFilterInternal_ExceptionThrown() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/test");
        when(request.getMethod()).thenReturn("GET");
        when(loggingHandler.shouldNotLog("/test", "GET")).thenReturn(false);

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        doThrow(new ServletException("Test Exception")).when(filterChain)
            .doFilter(any(ContentCachingRequestWrapper.class), any(ContentCachingResponseWrapper.class));

        // Act & Assert
        try {
            loggingFilter.doFilterInternal(wrappedRequest, wrappedResponse, filterChain);
        } catch (ServletException e) {
            // Exception should be thrown
            assertEquals("Test Exception", e.getMessage());
        }

        verify(filterChain, times(1)).doFilter(any(ContentCachingRequestWrapper.class),
            any(ContentCachingResponseWrapper.class));
        verify(loggingHandler, never()).logRequest(any(String.class), any(URI.class), any(HttpHeaders.class),
            any(byte[].class), any(Source.class));
        verify(loggingHandler, never()).logResponse(any(String.class), any(URI.class), anyInt(), any(HttpHeaders.class),
            any(byte[].class), any(Source.class));
    }

    @Test
    void testDoFilterInternal_LogResponseWhenStatusSetLater() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/test");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost/test"));
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeaderNames()).thenReturn(Collections.enumeration(Collections.emptyList()));
        when(response.getHeaderNames()).thenReturn(Collections.emptyList());
        when(loggingHandler.shouldNotLog("/test", "GET")).thenReturn(false);

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
        wrappedResponse.setStatus(404);

        // Act
        loggingFilter.doFilterInternal(wrappedRequest, wrappedResponse, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(any(ContentCachingRequestWrapper.class),
            any(ContentCachingResponseWrapper.class));
        verify(loggingHandler, times(1)).logRequest(
            anyString(),
            any(URI.class),
            any(HttpHeaders.class),
            requestContentCaptor.capture(),
            any(Source.class)
        );
        verify(loggingHandler, times(1)).logResponse(
            anyString(),
            any(URI.class),
            anyInt(), // explicitly set status code
            any(HttpHeaders.class),
            responseContentCaptor.capture(),
            any(Source.class)
        );

        assertArrayEquals(wrappedRequest.getContentAsByteArray(), requestContentCaptor.getValue());
        assertArrayEquals(wrappedResponse.getContentAsByteArray(), responseContentCaptor.getValue());
    }

    @Test
    void testGetHeadersFromRequest() throws ServletException, IOException {
        // Arrange
        Enumeration<String> headerNames = Collections.enumeration(Collections.singletonList("Header-Name"));
        when(request.getHeaderNames()).thenReturn(headerNames);
        when(request.getHeader("Header-Name")).thenReturn("Header-Value");
        when(request.getRequestURI()).thenReturn("/test");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost/test"));
        when(request.getMethod()).thenReturn("GET");
        when(response.getHeaderNames()).thenReturn(Collections.emptyList());
        when(loggingHandler.shouldNotLog("/test", "GET")).thenReturn(false);

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        // Act
        loggingFilter.doFilterInternal(wrappedRequest, wrappedResponse, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(any(ContentCachingRequestWrapper.class),
            any(ContentCachingResponseWrapper.class));
        verify(loggingHandler, times(1)).logRequest(
            anyString(),
            any(URI.class),
            any(HttpHeaders.class),
            requestContentCaptor.capture(),
            any(Source.class)
        );
        verify(loggingHandler, times(1)).logResponse(
            anyString(),
            any(URI.class),
            anyInt(), // explicitly set status code
            any(HttpHeaders.class),
            responseContentCaptor.capture(),
            any(Source.class)
        );

        assertArrayEquals(wrappedRequest.getContentAsByteArray(), requestContentCaptor.getValue());
        assertArrayEquals(wrappedResponse.getContentAsByteArray(), responseContentCaptor.getValue());
    }

    @Test
    void testGetHeadersFromResponse() throws ServletException, IOException {
        // Arrange
        when(request.getRequestURI()).thenReturn("/test");
        when(request.getRequestURL()).thenReturn(new StringBuffer("http://localhost/test"));
        when(request.getMethod()).thenReturn("GET");
        when(request.getHeaderNames()).thenReturn(Collections.enumeration(Collections.emptyList()));
        when(response.getHeaderNames()).thenReturn(Collections.singletonList("Authorization"));
        when(response.getHeader("Authorization")).thenReturn("Header-Value");
        when(loggingHandler.shouldNotLog("/test", "GET")).thenReturn(false);

        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        // Act
        loggingFilter.doFilterInternal(wrappedRequest, wrappedResponse, filterChain);

        // Assert
        verify(filterChain, times(1)).doFilter(
            any(ContentCachingRequestWrapper.class), any(ContentCachingResponseWrapper.class));
        verify(loggingHandler, times(1)).logRequest(
            anyString(),
            any(URI.class),
            any(HttpHeaders.class),
            requestContentCaptor.capture(),
            any(Source.class)
        );
        verify(loggingHandler, times(1)).logResponse(
            anyString(),
            any(URI.class),
            anyInt(), // explicitly set status code
            any(HttpHeaders.class),
            responseContentCaptor.capture(),
            any(Source.class)
        );

        assertArrayEquals(wrappedRequest.getContentAsByteArray(), requestContentCaptor.getValue());
        assertArrayEquals(wrappedResponse.getContentAsByteArray(), responseContentCaptor.getValue());
    }
}