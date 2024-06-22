package io.github.susimsek.springaisamples.exception;

import static io.github.susimsek.springaisamples.ratelimit.RateLimitConstants.RATE_LIMIT_LIMIT_HEADER_NAME;
import static io.github.susimsek.springaisamples.ratelimit.RateLimitConstants.RATE_LIMIT_REMAINING_HEADER_NAME;
import static io.github.susimsek.springaisamples.ratelimit.RateLimitConstants.RATE_LIMIT_RESET_HEADER_NAME;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.core.IntervalFunction;
import io.github.susimsek.springaisamples.exception.idempotency.MissingIdempotencyKeyException;
import io.github.susimsek.springaisamples.exception.ratelimit.RateLimitExceededException;
import io.github.susimsek.springaisamples.exception.security.JwsException;
import io.github.susimsek.springaisamples.i18n.ParameterMessageSource;
import jakarta.validation.ConstraintViolationException;
import java.net.SocketTimeoutException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final ParameterMessageSource messageSource;
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(
        @NonNull HttpMediaTypeNotAcceptableException ex,
        @NonNull HttpHeaders headers,
        @NonNull HttpStatusCode status,
        @NonNull WebRequest request) {
        return createProblemDetailResponse(ex, status, ErrorConstants.MEDIA_TYPE_NOT_ACCEPTABLE, headers, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
        @NonNull HttpMediaTypeNotSupportedException ex,
        @NonNull HttpHeaders headers,
        @NonNull HttpStatusCode status,
        @NonNull WebRequest request) {
        return createProblemDetailResponse(ex, status, ErrorConstants.MEDIA_TYPE_NOT_SUPPORTED, headers, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
        @NonNull HttpRequestMethodNotSupportedException ex,
        @NonNull HttpHeaders headers,
        @NonNull HttpStatusCode status,
        @NonNull WebRequest request) {
        return createProblemDetailResponse(ex, status, ErrorConstants.REQUEST_METHOD_NOT_SUPPORTED, headers, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
        @NonNull HttpMessageNotReadableException ex,
        @NonNull HttpHeaders headers,
        @NonNull HttpStatusCode status,
        @NonNull WebRequest request) {
        return createProblemDetailResponse(ex, status, ErrorConstants.MESSAGE_NOT_READABLE, headers, request);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
        @NonNull TypeMismatchException ex,
        @NonNull HttpHeaders headers,
        @NonNull HttpStatusCode status,
        @NonNull WebRequest request) {
        return createProblemDetailResponse(ex, status, ErrorConstants.TYPE_MISMATCH, headers, request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
        @NonNull MissingServletRequestParameterException ex,
        @NonNull HttpHeaders headers,
        @NonNull HttpStatusCode status,
        @NonNull WebRequest request) {
        Locale locale = request.getLocale();
        Map<String, String> namedArgs = Map.of("paramName", ex.getParameterName());
        String errorMessage = messageSource.getMessageWithNamedArgs(
            ErrorConstants.MISSING_PARAMETER, namedArgs, locale);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, errorMessage);
        return handleExceptionInternal(ex, problem, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(
        @NonNull MissingServletRequestPartException ex,
        @NonNull HttpHeaders headers,
        @NonNull HttpStatusCode status,
        @NonNull WebRequest request) {
        Locale locale = request.getLocale();
        Map<String, String> namedArgs = Map.of("partName", ex.getRequestPartName());
        String errorMessage = messageSource.getMessageWithNamedArgs(
            ErrorConstants.MISSING_PART, namedArgs, locale);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, errorMessage);
        return handleExceptionInternal(ex, problem, headers, status, request);
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

        String errorMessage = messageSource.getMessage(ErrorConstants.VALIDATION_ERROR);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, errorMessage);
        problem.setProperty(ErrorConstants.PROBLEM_VIOLATION_KEY, violations);
        return handleExceptionInternal(ex, problem, headers, status, request);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> handleConstraintViolationException(
        @NonNull ConstraintViolationException ex,
        @NonNull WebRequest request) {
        List<Violation> violations = ex.getConstraintViolations().stream().map(Violation::new).toList();

        String errorMessage = messageSource.getMessage(ErrorConstants.VALIDATION_ERROR);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, errorMessage);
        problem.setProperty(ErrorConstants.PROBLEM_VIOLATION_KEY, violations);
        return handleExceptionInternal(ex, problem, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(MultipartException.class)
    protected ResponseEntity<Object> handleMultipartException(@NonNull MultipartException ex,
                                                              @NonNull WebRequest request) {
        return createProblemDetailResponse(ex, HttpStatus.BAD_REQUEST,
            ErrorConstants.MULTIPART_ERROR, new HttpHeaders(), request);
    }

    @ExceptionHandler(MissingIdempotencyKeyException.class)
    protected ResponseEntity<Object> handleMissingIdempotencyKeyException(@NonNull MissingIdempotencyKeyException ex,
                                                        @NonNull WebRequest request) {
        return createProblemDetailResponse(ex, HttpStatus.BAD_REQUEST,
            ErrorConstants.IDEMPOTENCY_KEY_MISSING, new HttpHeaders(), request);
    }

    @ExceptionHandler(AuthenticationException.class)
    protected ResponseEntity<Object> handleAuthentication(@NonNull AuthenticationException ex,
                                                          @NonNull WebRequest request) {
        return createProblemDetailResponse(ex, HttpStatus.UNAUTHORIZED,
            ErrorConstants.AUTHENTICATION, new HttpHeaders(), request);
    }

    @ExceptionHandler(JwtException.class)
    protected ResponseEntity<Object> handleJwtException(@NonNull JwtException ex,
                                                        @NonNull WebRequest request) {
        return createProblemDetailResponse(ex, HttpStatus.UNAUTHORIZED,
            ErrorConstants.AUTHENTICATION, new HttpHeaders(), request);
    }

    @ExceptionHandler(JwsException.class)
    protected ResponseEntity<Object> handleJwsException(@NonNull JwsException ex,
                                                        @NonNull WebRequest request) {
        return createProblemDetailResponse(ex, HttpStatus.FORBIDDEN,
            ErrorConstants.SIGNATURE, new HttpHeaders(), request);
    }

    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<Object> handleAccessDenied(@NonNull AccessDeniedException ex,
                                                        @NonNull WebRequest request) {
        return createProblemDetailResponse(ex, HttpStatus.FORBIDDEN,
            ErrorConstants.ACCESS_DENIED, new HttpHeaders(), request);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
        @NonNull NoHandlerFoundException ex,
        @NonNull HttpHeaders headers,
        @NonNull HttpStatusCode status,
        @NonNull WebRequest request) {
        return createProblemDetailResponse(ex, HttpStatus.NOT_FOUND,
            ErrorConstants.NO_HANDLER_FOUND, headers, request);
    }

    @ExceptionHandler(RateLimitExceededException.class)
    protected ResponseEntity<Object> handleRateLimitExceededException(
        @NonNull RateLimitExceededException ex,
        @NonNull WebRequest request) {

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.RETRY_AFTER, String.valueOf(ex.getWaitTime()));
        headers.add(RATE_LIMIT_LIMIT_HEADER_NAME, String.valueOf(ex.getLimitForPeriod()));
        headers.add(RATE_LIMIT_REMAINING_HEADER_NAME, String.valueOf(ex.getAvailablePermissions()));
        headers.add(RATE_LIMIT_RESET_HEADER_NAME, String.valueOf(ex.getResetTime()));

        return createProblemDetailResponse(ex, HttpStatus.TOO_MANY_REQUESTS,
            ErrorConstants.RATE_LIMITING_ERROR,  headers, request);
    }

    @ExceptionHandler(ResourceException.class)
    public ResponseEntity<Object> handleResourceException(@NonNull ResourceException ex,
                                                          @NonNull WebRequest request) {
        Locale locale = request.getLocale();
        String localizedResourceName = messageSource.getMessage(
            "resource." + ex.getResourceName().toLowerCase(), null, locale);
        String searchCriteria = messageSource.getMessage(
            "search.criteria." + ex.getSearchCriteria().toLowerCase(), null, locale);
        String errorMessage = messageSource.getMessageWithNamedArgs(
            ex.getMessage(), createNamedArgs(localizedResourceName,
                searchCriteria, ex.getSearchValue()), locale);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(ex.getStatus(), errorMessage);
        return handleExceptionInternal(ex, problem, new HttpHeaders(), ex.getStatus(), request);
    }


    @ExceptionHandler(LocalizedException.class)
    public ResponseEntity<Object> handleLocalizedException(@NonNull LocalizedException ex,
                                                           @NonNull WebRequest request) {
        String errorMessage = resolveErrorMessage(ex, request.getLocale());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(ex.getStatus(), errorMessage);
        return handleExceptionInternal(ex, problem, new HttpHeaders(), ex.getStatus(), request);
    }

    @ExceptionHandler(ServerErrorException.class)
    public ResponseEntity<Object> handleServerErrorException(@NonNull ServerErrorException ex,
                                                             @NonNull WebRequest request) {
        return createProblemDetailResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR,
            ErrorConstants.INTERNAL_SERVER_ERROR, new HttpHeaders(), request);
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<Object> handleUnsupportedOperationException(
        @NonNull UnsupportedOperationException ex,
        @NonNull WebRequest request) {
        return createProblemDetailResponse(ex, HttpStatus.NOT_IMPLEMENTED,
            ErrorConstants.UNSUPPORTED_OPERATION, new HttpHeaders(), request);
    }

    @ExceptionHandler(SocketTimeoutException.class)
    public ResponseEntity<Object> handleSocketTimeoutException(@NonNull SocketTimeoutException ex,
                                                                  @NonNull WebRequest request) {
        return createProblemDetailResponse(ex, HttpStatus.GATEWAY_TIMEOUT,
            ErrorConstants.GATEWAY_TIMEOUT, new HttpHeaders(), request);
    }

    @ExceptionHandler(CallNotPermittedException.class)
    public ResponseEntity<Object> handleCallNotPermittedException(@NonNull CallNotPermittedException ex,
                                                                  @NonNull WebRequest request) {
        String circuitBreakerName = ex.getCausingCircuitBreakerName();
        CircuitBreaker circuitBreaker = circuitBreakerRegistry
            .circuitBreaker(circuitBreakerName);
        IntervalFunction intervalFunction = circuitBreaker.getCircuitBreakerConfig()
            .getWaitIntervalFunctionInOpenState();
        long waitDurationMillis = intervalFunction.apply(1);
        Duration waitDuration = Duration.ofMillis(waitDurationMillis);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.RETRY_AFTER, String.valueOf(waitDuration.getSeconds()));
        return createProblemDetailResponse(ex, HttpStatus.SERVICE_UNAVAILABLE,
            ErrorConstants.CIRCUIT_BREAKER_ERROR, headers, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllExceptions(
        @NonNull Exception ex,
        @NonNull WebRequest request) {
        return createProblemDetailResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR,
            ErrorConstants.INTERNAL_SERVER_ERROR, new HttpHeaders(), request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
        @NonNull Exception ex,
        Object body,
        @NonNull HttpHeaders headers,
        @NonNull HttpStatusCode status,
        @NonNull WebRequest request) {
        log.error("An exception occurred, which will cause a {} response", status, ex);
        return super.handleExceptionInternal(ex, body, headers, status, request);
    }

    private ResponseEntity<Object> createProblemDetailResponse(
        Exception ex,
        HttpStatusCode status,
        String messageKey,
        HttpHeaders headers,
        WebRequest request) {
        String errorMessage = messageSource.getMessage(messageKey, null, request.getLocale());
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, errorMessage);
        return handleExceptionInternal(ex, problem, headers, status, request);
    }

    private String resolveErrorMessage(LocalizedException ex, Locale locale) {
        if (ex.getNamedArgs() != null) {
            return messageSource.getMessageWithNamedArgs(ex.getMessage(), ex.getNamedArgs(), locale);
        } else {
            return messageSource.getMessage(ex.getMessage(), ex.getArgs(), locale);
        }
    }

    private Map<String, String> createNamedArgs(String resourceName,
                                                       String searchCriteria,
                                                       Object searchValue) {
        Map<String, String> namedArgs = new HashMap<>();
        namedArgs.put("resource", resourceName);
        namedArgs.put("criteria", searchCriteria);
        namedArgs.put("value", searchValue.toString());
        return namedArgs;
    }
}