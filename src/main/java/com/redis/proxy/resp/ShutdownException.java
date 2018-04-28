package com.redis.proxy.resp;

import java.io.IOException;
import java.util.Collection;

class ShutdownException extends IOException {
    private Collection<IOException> exceptions;

    ShutdownException(Collection<IOException> e) {
        super(String.format("Multiple (%d) exceptions", e.size()));
        exceptions = e;
    }
}