package com.redis.proxy.resp.protocol;


import java.nio.ByteBuffer;
import java.util.Deque;

/**
 * Not part of the RESP spec.  Used internally within JRESP and Redis clients based on JRESP to send exceptions
 * to waiting functions.
 */
public class ClientErr implements RespType {
    private Throwable error;

    public ClientErr(Throwable error) {
        if (error == null) {
            throw new NullPointerException("error");
        } else {
            this.error = error;
        }
    }

    public String toString() {
        return String.format("%s[%s]", getClass().getName(), error.getMessage());
    }

    @Override
    public void writeBytes(Deque<ByteBuffer> out) {
        throw new UnsupportedOperationException("This cannot be written anywhere, this is only to be used within the client");
    }

    @Override
    public Object unwrap() {
        return error;
    }
}