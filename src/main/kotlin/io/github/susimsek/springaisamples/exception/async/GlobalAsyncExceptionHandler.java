package io.github.susimsek.springaisamples.exception.async;

import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GlobalAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    @Override
    public void handleUncaughtException(Throwable throwable, Method method, Object... obj) {
        log.error("Exception message - {}", throwable.getMessage());
        log.error("Method name - {}", method.getName());
        for (Object param : obj) {
            log.error("Parameter value - {}", param);
        }
    }
}