package com.redis.proxy.resp.protocol;


import java.nio.ByteBuffer;
import java.util.Deque;

public class Int implements RespType {
    private long payload;

    public Int(long payload) {
        this.payload = payload;
    }

    public String toString() {
        return String.format("%s[%s]", getClass().getName(), payload);
    }

    @Override
    public void writeBytes(Deque<ByteBuffer> out) {
        byte[] bytes = Resp.longToByteArray(payload);
        int size = 1 + bytes.length + 2;
        ByteBuffer o = Resp.buffer(out, size);
        o.put((byte) ':');
        o.put(bytes);
        o.put(Resp.CRLF);
    }

    @Override
    public Object unwrap() {
        return payload;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(payload);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Int) {
            Int i = (Int)o;
            return payload == i.payload;
        } else {
            return false;
        }
    }
}