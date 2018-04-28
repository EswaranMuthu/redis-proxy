package com.redis.proxy.resp;

public class ConnectionException extends Exception {
    public ConnectionException(String s) {
        super(s);
    }

    public ConnectionException(Exception e) {
        super(e);
    }
}