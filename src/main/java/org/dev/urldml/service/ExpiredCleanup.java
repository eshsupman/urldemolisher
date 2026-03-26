package org.dev.urldml.service;

import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.dev.urldml.repository.UrlRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class ExpiredCleanup {
    private static final Logger log = LoggerFactory.getLogger(ExpiredCleanup.class);
    private final UrlRepository urlRepository;

    public ExpiredCleanup(UrlRepository urlRepository){
        this.urlRepository = urlRepository;
    }
    @SchedulerLock(name = "deleteExpiredUrls")
    @Scheduled(cron = "0 0 0 1 * * ")
    public void deleteExpiredLinks() {
        log.info("Starting cleanup of expired links");
        int totalDeleted = 0;
        int batch;
        do {
            batch = urlRepository.deleteExpired(LocalDateTime.now());
            totalDeleted += batch;
        } while (batch == 1000);
        log.info("Cleanup finished, deleted {} expired links", totalDeleted);
    }

}
