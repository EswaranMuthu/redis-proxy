package com.redis.proxy.resp.protocol;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Deque;

public class SimpleStr implements RespType {
    private String payload;

    public SimpleStr(String str) {
        payload = str;
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

            o.put((byte) '+');
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

    @Override
    public int hashCode() {
        return payload.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof SimpleStr) {
            SimpleStr ss = (SimpleStr)o;
            return payload.equals(ss.payload);
        } else {
            return false;
        }
    }
}