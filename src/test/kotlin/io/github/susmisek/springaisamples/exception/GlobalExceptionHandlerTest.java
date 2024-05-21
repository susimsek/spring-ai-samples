package io.github.susmisek.springaisamples.exception;

import static io.github.susmisek.springaisamples.exception.ErrorConstants.ERR_INTERNAL_SERVER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;

import jakarta.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    void handleHttpMediaTypeNotAcceptable() {
        HttpMediaTypeNotAcceptableException ex = new HttpMediaTypeNotAcceptableException("Not Acceptable");
        HttpHeaders headers = new HttpHeaders();
        WebRequest request = mock(WebRequest.class);
        ResponseEntity<Object> responseEntity = exceptionHandler.handleHttpMediaTypeNotAcceptable(ex, headers, HttpStatus.NOT_ACCEPTABLE, request);
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.NOT_ACCEPTABLE, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }

    @Test
    void handleHttpMediaTypeNotSupported() {
        List<MediaType> supportedMediaTypes = Arrays.asList(MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML);
        HttpMediaTypeNotSupportedException ex = new HttpMediaTypeNotSupportedException(MediaType.TEXT_PLAIN, supportedMediaTypes);
        HttpHeaders headers = new HttpHeaders();
        WebRequest request = mock(WebRequest.class);
        ResponseEntity<Object> responseEntity = exceptionHandler.handleHttpMediaTypeNotSupported(ex, headers, HttpStatus.UNSUPPORTED_MEDIA_TYPE, request);
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }

    @Test
    void handleHttpRequestMethodNotSupported_WithSupportedMethods() {
        // Test case for handling HttpRequestMethodNotSupportedException with supported methods
        List<String> supportedMethods = Arrays.asList("GET", "POST");
        HttpRequestMethodNotSupportedException ex = new HttpRequestMethodNotSupportedException("Method Not Allowed", supportedMethods);
        HttpHeaders headers = new HttpHeaders();
        headers.setAllow(Set.of(HttpMethod.GET));
        WebRequest request = mock(WebRequest.class);
        ResponseEntity<Object> responseEntity = exceptionHandler.handleHttpRequestMethodNotSupported(ex, headers, HttpStatus.METHOD_NOT_ALLOWED, request);
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertFalse(responseEntity.getHeaders().get(HttpHeaders.ALLOW).isEmpty());
    }

    @Test
    void handleHttpRequestMethodNotSupported_WithoutSupportedMethods() {
        HttpRequestMethodNotSupportedException ex = new HttpRequestMethodNotSupportedException("Method Not Allowed", Collections.emptyList());
        HttpHeaders headers = new HttpHeaders();
        WebRequest request = mock(WebRequest.class);
        ResponseEntity<Object> responseEntity = exceptionHandler.handleHttpRequestMethodNotSupported(ex, headers, HttpStatus.METHOD_NOT_ALLOWED, request);
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertNull(responseEntity.getHeaders().get(HttpHeaders.ALLOW));
    }

    @Test
    void handleHttpMediaTypeNotSupported_EmptySupportedMediaTypes() {
        HttpMediaTypeNotSupportedException ex = new HttpMediaTypeNotSupportedException(MediaType.TEXT_PLAIN, Collections.emptyList());
        HttpHeaders headers = new HttpHeaders();
        WebRequest request = mock(WebRequest.class);
        ResponseEntity<Object> responseEntity = exceptionHandler.handleHttpMediaTypeNotSupported(ex, headers, HttpStatus.UNSUPPORTED_MEDIA_TYPE, request);
        assertNotNull(responseEntity);
        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
    }

    @Test
    void handleHttpRequestMethodNotSupported_NullSupportedMethods() {
        HttpRequestMethodNotSupportedException ex = new HttpRequestMethodNotSupportedException("Method Not Allowed", null);
        HttpHeaders headers = new HttpHeaders();
        WebRequest request = mock(WebRequest.class);
        ResponseEntity<Object> responseEntity = exceptionHandler.handleHttpRequestMethodNotSupported(ex, headers, HttpStatus.METHOD_NOT_ALLOWED, request);
        assertEquals(HttpStatus.METHOD_NOT_ALLOWED, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertNull(responseEntity.getHeaders().get(HttpHeaders.ALLOW));
    }

    @Test
    void handleMethodArgumentNotValid() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        HttpHeaders headers = new HttpHeaders();
        WebRequest request = mock(WebRequest.class);
        ResponseEntity<Object> responseEntity = exceptionHandler.handleMethodArgumentNotValid(ex, headers, HttpStatus.BAD_REQUEST, request);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(ErrorConstants.ERR_VALIDATION, ((ProblemDetail) responseEntity.getBody()).getDetail());
    }

    @Test
    void handleConstraintViolationException() {
        ConstraintViolationException ex = mock(ConstraintViolationException.class);
        WebRequest request = mock(WebRequest.class);
        ResponseEntity<Object> responseEntity = exceptionHandler.handleConstraintViolationException(ex, request);
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
        assertNotNull(responseEntity.getBody());
        assertEquals(ErrorConstants.ERR_VALIDATION, ((ProblemDetail) responseEntity.getBody()).getDetail());
    }

    @Test
    void handleAll() {
        Exception ex = new Exception("internal error");
        WebRequest request = mock(WebRequest.class);
        ResponseEntity<Object> responseEntity = exceptionHandler.handleAll(ex, request);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals(ERR_INTERNAL_SERVER, ((ProblemDetail) responseEntity.getBody()).getDetail());
    }
}
