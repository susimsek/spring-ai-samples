package io.github.susimsek.springaisamples.exception;

import io.github.susimsek.springaisamples.exception.idempotency.MissingIdempotencyKeyException;
import io.github.susimsek.springaisamples.exception.ratelimit.RateLimitExceededException;
import io.github.susimsek.springaisamples.exception.security.JwsException;
import io.github.susimsek.springaisamples.i18n.ParameterMessageSource;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final ParameterMessageSource messageSource;

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

        return createProblemDetailResponse(ex, HttpStatus.TOO_MANY_REQUESTS,
            ErrorConstants.RATE_LIMITING_ERROR,  new HttpHeaders(), request);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(@NonNull ResourceNotFoundException ex,
                                                                  @NonNull WebRequest request) {
        return handleResourceException(ex.getResourceName(), ex.getSearchCriteria(),
           ex.getSearchValue(), HttpStatus.NOT_FOUND, ex, request);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<Object> handleResourceAlreadyExistsException(@NonNull ResourceAlreadyExistsException ex,
                                                                  @NonNull WebRequest request) {
        return handleResourceException(ex.getResourceName(), ex.getSearchCriteria(),
            ex.getSearchValue(), HttpStatus.CONFLICT, ex, request);
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

    protected ResponseEntity<Object> handleResourceException(
        String resourceName,
        String searchCriteria,
        Object searchValue,
        HttpStatusCode status,
        LocalizedException ex,
        @NonNull WebRequest request) {
        Locale locale = request.getLocale();
        String localizedResourceName = messageSource.getMessage(
            "resource." + resourceName.toLowerCase(), null, locale);
        String localizedSearchCriteria = messageSource.getMessage(
            "search.criteria." + searchCriteria.toLowerCase(), null, locale);
        String errorMessage = messageSource.getMessageWithNamedArgs(
            ex.getMessage(), createNamedArgs(localizedResourceName,
                localizedSearchCriteria, searchValue), locale);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, errorMessage);
        return handleExceptionInternal(ex, problem, new HttpHeaders(), status, request);
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