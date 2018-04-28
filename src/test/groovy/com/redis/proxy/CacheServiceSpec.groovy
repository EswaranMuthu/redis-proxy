package com.redis.proxy

import com.redis.proxy.cache.CacheImpl
import com.redis.proxy.cache.CacheMemoryManager
import spock.lang.Specification

class CacheServiceSpec extends Specification {

    def "Test cache service "() {
        given:
        CacheMemoryManager cacheMemory = CacheMemoryManager.cacheMemoryManagerFactory()
        CacheImpl cache = new CacheImpl()

        when "remove LRU record ":
        cache.put("key1", "value1",4)
        cache.put("key2", "value2",4)
        cache.put("key3", "value3",4)
        cache.put("key4", "value4",4)
        cache.put("key5", "value5",4)
        then:
        cache.getValue("key1") == null

        when "Get value from key":
        String value = cache.getValue("key1")
        then:
        value != null
    }


}
