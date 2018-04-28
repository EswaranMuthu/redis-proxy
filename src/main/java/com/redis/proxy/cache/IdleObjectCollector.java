package com.redis.proxy.cache;

import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.scheduling.annotation.*;

@Configuration
@EnableScheduling
@Slf4j
public class IdleObjectCollector {

    @Value("${redis.cache.expiry}")
    private String expiry;

    /**
     * IdleObjectCollector (Scheduler) runs every 1 hr, to clear
     */
    @Scheduled(fixedDelay = 600000, initialDelay= 600000)
    public void purgePendingCarts() {
        log.info("clear records that are idle for {} milli seconds", expiry);
        CacheMemoryManager cacheMemoryManager = CacheMemoryManager.cacheMemoryManagerFactory();
        cacheMemoryManager.removeIdleObjects(Long.parseLong(expiry));

        log.info("Purging Pending Cart completed-cron-jobs-all-regions-" + System.currentTimeMillis() / 1000);
    }

}
