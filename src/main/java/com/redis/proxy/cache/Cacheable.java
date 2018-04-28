package com.redis.proxy.cache;

import java.lang.annotation.*;

/**
 * Custom annotation for Cache
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Cacheable {
    public String name() default "default";
}
