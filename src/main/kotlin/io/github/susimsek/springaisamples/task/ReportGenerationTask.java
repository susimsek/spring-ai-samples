package io.github.susimsek.springaisamples.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "spring.task.tasks.report.enabled", havingValue = "true", matchIfMissing = true)
@Slf4j
public class ReportGenerationTask {

    @Scheduled(cron = "${spring.task.tasks.report.cron}")
    public void generateReports() {
        log.info("Report generation task executed at {}", System.currentTimeMillis());
    }
}