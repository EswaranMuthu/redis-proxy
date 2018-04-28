package com.redis.proxy.resp.protocol;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Deque;

public class Err implements RespType {
    private String payload;

    public Err(String payload) {
        this.payload = payload;
    }

    public String toString() {
        return String.format("%s[%s]", getClass().getName(), payload);
    }

    @Override
    public void writeBytes(Deque<ByteBuffer> out) {
        try {
            byte[] bytes = payload.getBytes("UTF-8");
            int size = 1 + bytes.length + 2;

            ByteBuffer o = Resp.buffer(out, size);

            o.put((byte)'-');
            o.put(bytes);
            o.put(Resp.CRLF);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public Object unwrap() {
        return payload;
    }
}