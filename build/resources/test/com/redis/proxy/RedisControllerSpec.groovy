package com.redis.proxy

import com.redis.proxy.cache.RedisProxyController
import spock.lang.Specification

class RedisControllerSpec extends Specification {

    def "Redis controller test"(){

        given:
        RedisProxyController redisProxyController = new RedisProxyController()
    }
}
