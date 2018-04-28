package com.redis.proxy.cache;

import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;
import org.apache.tomcat.util.threads.TaskQueue;
import org.springframework.beans.factory.annotation.*;

/**
 * Cache memory Manager, This class manages memory of redis proxy cache
 */
public class CacheMemoryManager {

    private Map<String, ValueObject> redisProxy = new ConcurrentHashMap<String, ValueObject>();
    private Queue<String> queue = new ConcurrentLinkedQueue<String>();
    private final static CacheMemoryManager cacheMemoryManager = new CacheMemoryManager();

    private CacheMemoryManager(){}

    public static CacheMemoryManager cacheMemoryManagerFactory(){
        return cacheMemoryManager;
    }

    public int getSize(){
        return redisProxy.size();
    }

    public String getValue(String key){
        if(redisProxy.containsKey(key)){
            extendTTL(key);
            ValueObject valueObject = redisProxy.get(key);
            return valueObject.getValue();
        }
        return null;
    }

    public void extendTTL(String key){
        queue.remove(key);
        queue.add(key);
        redisProxy.get(key).setValueCreated(ZonedDateTime.now());
    }

    public void writeValue(String key, String value){
        redisProxy.put(key, ValueObject.builder()
                            .value(value)
                            .valueCreated(ZonedDateTime.now())
                            . build());
    }

    public void removeLRU(){
        String key = queue.remove();
        redisProxy.remove(key);
    }

    public synchronized  void writeValue(String key, String value, int capacity){
        if((getSize() + 1) == capacity ){
            removeLRU();
            writeValue(key, value);
        }
    }

    public void clearCache(){
        redisProxy.clear();
    }

    public void removeIdleObjects(Long expiry){
        List<String> keyList = redisProxy.entrySet()
                                         .stream()
                                         .filter(e-> (System.currentTimeMillis() - e.getValue().getValueCreated().toInstant().toEpochMilli()) > expiry)
                                         .map(e-> e.getKey())
                                         .collect(Collectors.toList());
        redisProxy.keySet().removeAll(keyList);
        queue.removeAll(keyList);
    }
}
