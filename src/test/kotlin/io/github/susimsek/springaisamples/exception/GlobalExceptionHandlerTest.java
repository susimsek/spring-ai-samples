package io.github.susimsek.springaisamples.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.github.susimsek.springaisamples.i18n.ParameterMessageSource;
import jakarta.validation.ConstraintViolationException;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private ParameterMessageSource messageSource;

    @InjectMocks
    private GlobalExceptionHandler exceptionHandler;

    @Test
    void handleHttpMediaTypeNotAcceptable() {
        HttpMediaTypeNotAcceptableException ex = new HttpMediaTypeNotAcceptableException("message");
        HttpHeaders headers = new HttpHeaders();
        WebRequest request = mock(WebRequest.class);

        when(messageSource.getMessage(anyString())).thenReturn("Requested media type is not acceptable.");

        ResponseEntity<Object> response =
            exceptionHandler.handleHttpMediaTypeNotAcceptable(ex, headers, HttpStatus.NOT_ACCEPTABLE, request);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
        var problemDetail = ((ProblemDetail) response.getBody());
        assertNotNull(problemDetail);
        assertEquals("Requested media type is not acceptable.", problemDetail.getDetail());
    }

    @Test
    void handleHttpMediaTypeNotSupportedException() {
        HttpMediaTypeNotSupportedException ex = new HttpMediaTypeNotSupportedException("message",
            Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpHeaders headers = new HttpHeaders();
        WebRequest request = mock(WebRequest.class);

        when(messageSource.getMessage(anyString())).thenReturn("Requested media type is not supported.");

        ResponseEntity<Object> response = exceptionHandler.handleHttpMediaTypeNotSupported(
            ex, headers, HttpStatus.UNSUPPORTED_MEDIA_TYPE, request);
        assertNotNull(response);
        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, response.getStatusCode());
        var problemDetail = ((ProblemDetail) response.getBody());
        assertNotNull(problemDetail);
        assertEquals("Requested media type is not supported.", problemDetail.getDetail());
    }

    @Test
    void handleHttpRequestMethodNotSupportedException() {
        HttpRequestMethodNotSupportedException ex = new HttpRequestMethodNotSupportedException("message");
        HttpHeaders headers = new HttpHeaders();
        WebRequest request = mock(WebRequest.class);

        when(messageSource.getMessage(anyString())).thenReturn("Requested HTTP method is not supported.");

        ResponseEntity<Object> response =
            exceptionHandler.handleHttpRequestMethodNotSupported(ex, headers, HttpStatus.METHOD_NOT_ALLOWED, request);
        assertNotNull(response);
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, response.getStatusCode());
        var problemDetail = ((ProblemDetail) response.getBody());
        assertNotNull(problemDetail);
        assertEquals("Requested HTTP method is not supported.", problemDetail.getDetail());
    }

    @Test
    void handleMethodArgumentNotValid() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);

        HttpHeaders headers = new HttpHeaders();
        WebRequest request = mock(WebRequest.class);

        when(messageSource.getMessage(anyString())).thenReturn("Validation error occurred.");

        ResponseEntity<Object> response =
            exceptionHandler.handleMethodArgumentNotValid(ex, headers, HttpStatus.BAD_REQUEST, request);
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        var problemDetail = ((ProblemDetail) response.getBody());
        assertNotNull(problemDetail);
        assertEquals("Validation error occurred.", problemDetail.getDetail());
    }

    @Test
    void handleConstraintViolationException() {
        ConstraintViolationException ex = mock(ConstraintViolationException.class);
        WebRequest request = mock(WebRequest.class);

        when(messageSource.getMessage(anyString())).thenReturn("Validation error occurred.");

        ResponseEntity<Object> response = exceptionHandler.handleConstraintViolationException(ex, request);
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        var problemDetail = ((ProblemDetail) response.getBody());
        assertNotNull(problemDetail);
        assertEquals("Validation error occurred.", problemDetail.getDetail());
    }

    @Test
    void handleNoHandlerFoundException() {
        NoHandlerFoundException ex = new NoHandlerFoundException("GET", "/non-existent", new HttpHeaders());
        WebRequest request = mock(WebRequest.class);

        when(messageSource.getMessage(anyString())).thenReturn("No handler found for the requested URL.");

        ResponseEntity<Object> response = exceptionHandler.handleNoHandlerFoundException(
            ex, new HttpHeaders(), HttpStatus.NOT_FOUND, request);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        var problemDetail = ((ProblemDetail) response.getBody());
        assertNotNull(problemDetail);
        assertEquals("No handler found for the requested URL.", problemDetail.getDetail());
    }


    @Test
    void handleAllExceptions() {
        Exception ex = new Exception("message");
        WebRequest request = mock(WebRequest.class);

        when(messageSource.getMessage(anyString())).thenReturn(
            "An unexpected condition was encountered on the server.");

        ResponseEntity<Object> response = exceptionHandler.handleAllExceptions(ex, request);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        var problemDetail = ((ProblemDetail) response.getBody());
        assertNotNull(problemDetail);
        assertEquals("An unexpected condition was encountered on the server.", problemDetail.getDetail());
    }
}
