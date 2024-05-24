package io.github.susmisek.springaisamples.exception;

import static io.github.susmisek.springaisamples.exception.ErrorConstants.ERR_INTERNAL_SERVER;
import static io.github.susmisek.springaisamples.exception.ErrorConstants.ERR_VALIDATION;
import static io.github.susmisek.springaisamples.exception.ErrorConstants.PROBLEM_VIOLATION_KEY;

import jakarta.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private MessageSource messageSource;

    @Override
    public void setMessageSource(@NonNull MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(
        @NonNull HttpMediaTypeNotAcceptableException ex,
        @NonNull HttpHeaders headers,
        @NonNull HttpStatusCode status,
        @NonNull WebRequest request) {
        return buildResponseEntity(ex, HttpStatus.NOT_ACCEPTABLE, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
        @NonNull HttpMediaTypeNotSupportedException ex,
        @NonNull HttpHeaders headers,
        @NonNull HttpStatusCode status,
        @NonNull WebRequest request) {
        return buildResponseEntity(ex, HttpStatus.UNSUPPORTED_MEDIA_TYPE, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
        @NonNull HttpRequestMethodNotSupportedException ex,
        @NonNull HttpHeaders headers,
        @NonNull HttpStatusCode status,
        @NonNull WebRequest request) {
        return buildResponseEntity(ex, HttpStatus.METHOD_NOT_ALLOWED, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        @NonNull MethodArgumentNotValidException ex,
        @NonNull HttpHeaders headers,
        @NonNull HttpStatusCode status,
        @NonNull WebRequest request) {
        List<Violation> violations = Stream.concat(
            ex.getFieldErrors().stream().map(Violation::new),
            ex.getGlobalErrors().stream().map(Violation::new)
        ).toList();

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ERR_VALIDATION);
        problem.setProperty(PROBLEM_VIOLATION_KEY, violations);

        return buildResponseEntity(ex, problem, headers, status, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstraintViolationException(
        @NonNull ConstraintViolationException ex,
        @NonNull WebRequest request) {
        List<Violation> violations = ex.getConstraintViolations().stream().map(Violation::new).toList();

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ERR_VALIDATION);
        problem.setProperty(PROBLEM_VIOLATION_KEY, violations);

        return buildResponseEntity(ex, problem, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAll(
        @NonNull Exception ex,
        @NonNull WebRequest webRequest) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, ERR_INTERNAL_SERVER);
        return buildResponseEntity(ex, problem, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, webRequest);
    }

    private ResponseEntity<Object> buildResponseEntity(
        @NonNull Exception ex,
        HttpStatus status,
        @NonNull HttpHeaders headers,
        @NonNull HttpStatusCode statusCode,
        @NonNull WebRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, ex.getMessage());
        return buildResponseEntity(ex, problem, headers, statusCode, request);
    }

    private ResponseEntity<Object> buildResponseEntity(
        @NonNull Exception ex,
        ProblemDetail problem,
        @NonNull HttpHeaders headers,
        @NonNull HttpStatusCode status,
        @NonNull WebRequest request) {
        if (status.is5xxServerError()) {
            log.error("An exception occurred, which will cause a {} response", status, ex);
        } else {
            log.warn("An exception occurred, which will cause a {} response", status, ex);
        }
        return super.handleExceptionInternal(ex, problem, headers, status, request);
    }
}
