package com.redis.proxy.cache;

import com.redis.proxy.resp.*;
import com.redis.proxy.resp.pool.*;
import com.redis.proxy.resp.protocol.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class RedisProxyController {

    @Value("${redis.host}")
    private String hostname ;
    @Value("${redis.port}")
    private String port;
    @Value("${redis.password}")
    private String password;

    @Cacheable
    @GetMapping("/redis/proxy/get")
    public String getValue(@RequestParam("key") String key) throws IOException, ConnectionException, InterruptedException {
        log.debug("The key value is {} ", key);
        if (key == null) {
            log.info("User provided key is null");
        }
        AtomicReference<String> value = new AtomicReference<>();
        AtomicBoolean isResponseReceived = new AtomicBoolean(false);

        Responses responses = new Responses() {
            @Override
            public void responseReceived(RespType response) {
                log.info("value received from Redis: " + response.unwrap());
                value.set((String) response.unwrap());
                isResponseReceived.set(true);
            }
        };
        Client client = new Client(hostname, Integer.parseInt(port));
        client.setPassword(password);
        Pool pool = Pool.poolFactory(client);
        pool.getShared().write(new Ary(Arrays.asList(new BulkStr(key))), responses);
        while (!isResponseReceived.get()) {
            Thread.sleep(200);
        }
        return value.get();
    }
}
