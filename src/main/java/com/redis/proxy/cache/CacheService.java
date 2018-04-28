package com.redis.proxy.cache;

import com.redis.proxy.resp.*;
import com.redis.proxy.resp.pool.*;
import com.redis.proxy.resp.protocol.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import lombok.extern.slf4j.*;
import org.aspectj.lang.*;
import org.aspectj.lang.annotation.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.web.bind.annotation.*;

@Component
@Slf4j
@Aspect
@Configuration
public class CacheService {
    @Autowired
    private CacheImpl cache;
    @Value("${redis.cache.capacity}")
    private String capacity;
    @Value("$(redis.cache.expiry)")
    private String expiry;

    /**
     * This AOP method will intercept all "get" call to redis, check if same key exist in CacheMemoryManager
     *
     * @param joinPoint
     * @return
     * @throws Throwable
     */
    @Around("@annotation(com.redis.proxy.cache.Cacheable)")
    public String getValue(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] objects = joinPoint.getArgs();
        String key = "";
        if (objects != null && objects.length > 0) {
            key = (String) objects[0];
        }
        String value = new String();
        value = cache.getValue(key);
        if (value == null) { // Redis cache does not has the value
            value = (String) joinPoint.proceed();
            cache.put(key, value, Integer.parseInt(capacity));
            return value;
        } else {
            cache.extendTTL(key);
            return value;
        }
    }
}
