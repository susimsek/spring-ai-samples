package io.github.susimsek.springaisamples.validation;


import io.github.resilience4j.spring6.spelresolver.SpelResolver;
import io.github.susimsek.springaisamples.exception.ValidationException;
import java.lang.reflect.Method;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class EmailValidationAspect {

    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    private final SpelResolver spelResolver;

    @Pointcut("@annotation(io.github.susimsek.springaisamples.validation.EmailValidation)")
    public void emailValidationPointcut() {
    }

    @Before("emailValidationPointcut() && @annotation(emailValidation)")
    public void validateEmail(JoinPoint joinPoint, EmailValidation emailValidation) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        Object[] args = joinPoint.getArgs();
        String emailExpression = emailValidation.emailExpression();

        String email = spelResolver.resolve(method, args, emailExpression);

        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException(emailValidation.message());
        }
        log.info("Valid email: {}", email);
    }
}