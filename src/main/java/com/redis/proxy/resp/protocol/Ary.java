package com.redis.proxy.resp.protocol;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.stream.Collectors;

public class Ary implements RespType {
    private List<RespType> payload;

    public Ary(List<RespType> payload) {
        this.payload = payload;
    }

    public Ary(RespType... payload) {
        this.payload = Arrays.asList(payload);
    }

    public String toString() {
        return String.format("%s[%s]", getClass().getName(), payload);
    }

    @Override
    public void writeBytes(Deque<ByteBuffer> out) {
        byte[] header = Resp.longToByteArray(payload.size());
        int size = 1 + header.length + 2;
        ByteBuffer o = Resp.buffer(out, size);
        o.put((byte)'*');
        o.put(header);
        o.put(Resp.CRLF);
        payload.stream().forEach(x -> x.writeBytes(out));
    }

    public List<RespType> raw() {
        return payload;
    }

    @Override
    public Object unwrap() {
        return payload.stream().map(RespType::unwrap).collect(Collectors.toList());
    }

    @Override
    public int hashCode() {
        return payload.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Ary) {
            Ary a = (Ary)o;
            return payload.equals(a.payload);
        } else {
            return false;
        }
    }
}