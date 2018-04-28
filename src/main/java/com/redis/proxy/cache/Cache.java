package com.redis.proxy.cache;

/**
 * Redis cache interface
 */
public interface Cache {

    public String getValue(String key);
    public void cacheEvict();
    public void evictLRU();
    public void extendTTL(String key);
    public void put(String key, String value, int capacity);
}
