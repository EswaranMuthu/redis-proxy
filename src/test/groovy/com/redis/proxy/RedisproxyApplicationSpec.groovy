package com.redis.proxy

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.context.*
import spock.lang.Specification;

@RunWith(SpringRunner.class)
@ContextConfiguration
@SuppressWarnings("all")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RedisproxyApplicationSpec extends  Specification{

    @Autowired
    WebApplicationContext webApplicationContext

    def "application starts up properly"() {
        expect:
        webApplicationContext != null
    }
}
