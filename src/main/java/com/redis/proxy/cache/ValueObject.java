package com.redis.proxy.cache;

import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import lombok.*;

@Builder
@Data
public class ValueObject {

    private String value;
    private ZonedDateTime valueCreated;

}
