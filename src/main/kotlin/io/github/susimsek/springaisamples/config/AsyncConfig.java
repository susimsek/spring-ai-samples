package io.github.susimsek.springaisamples.config;

import io.github.susimsek.springaisamples.exception.async.GlobalAsyncExceptionHandler;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import lombok.RequiredArgsConstructor;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration(proxyBeanMethods = false)
@EnableAsync
@EnableScheduling
@RequiredArgsConstructor
public class AsyncConfig implements AsyncConfigurer {


    private final TaskExecutionProperties taskExecutionProperties;
    private final GlobalAsyncExceptionHandler globalAsyncExceptionHandler;


    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        TaskExecutionProperties.Pool pool = taskExecutionProperties.getPool();
        executor.setCorePoolSize(pool.getCoreSize());
        executor.setMaxPoolSize(pool.getMaxSize());
        executor.setQueueCapacity(pool.getQueueCapacity());
        executor.setKeepAliveSeconds((int) pool.getKeepAlive().getSeconds());
        executor.setThreadNamePrefix(taskExecutionProperties.getThreadNamePrefix());

        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.setWaitForTasksToCompleteOnShutdown(taskExecutionProperties.getShutdown().isAwaitTermination());
        executor.setAwaitTerminationSeconds((int) taskExecutionProperties.getShutdown().getAwaitTerminationPeriod().getSeconds());
        executor.initialize();
        return executor;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return globalAsyncExceptionHandler;
    }
}