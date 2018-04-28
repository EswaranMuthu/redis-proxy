package com.redis.proxy.cache;

import lombok.*;
import lombok.extern.slf4j.*;
import org.aspectj.lang.*;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.beans.factory.annotation.Value;

/**
 * This class provides redis cache API implementation
 */

@Component
@Slf4j
@RequiredArgsConstructor
public class CacheImpl implements Cache{

    private CacheMemoryManager cacheMemoryManager = CacheMemoryManager.cacheMemoryManagerFactory();

    @Override
    public String getValue(String key){
        return cacheMemoryManager.getValue(key);
    }

    @Override
    public void cacheEvict() {
        cacheMemoryManager.clearCache();
    }

    @Override
    public void evictLRU() {
        cacheMemoryManager.removeLRU();
    }

    @Override
    public void extendTTL(String key) {
        cacheMemoryManager.extendTTL(key);
    }

    @Override
    public void put(String key, String value, int capacity){
        cacheMemoryManager.writeValue(key,value, capacity);
    }
}
