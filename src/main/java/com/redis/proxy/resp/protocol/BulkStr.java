package com.redis.proxy.resp.protocol;


import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.*;

public class BulkStr implements RespType {
    private static final Map<String, BulkStr> cache = Collections.synchronizedMap(new HashMap<>());

    private byte[] payload;

    public BulkStr(String s) {
        try {
            payload = s.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    public BulkStr(byte[] s) {
        payload = s;
    }

    public BulkStr() {
        this.payload = null;
    }

    public static BulkStr get(String str) {
        BulkStr val = cache.get(str);
        if (val == null) {
            val = new BulkStr(str);
            cache.put(str, val);
        }
        return val;
    }

    public String toString() {
        return String.format("%s[%s]", getClass().getName(), Arrays.toString(payload));
    }

    @Override
    public void writeBytes(Deque<ByteBuffer> out) {
        int size = 1;
        byte[] header;
        if (payload == null) {
            header = Resp.longToByteArray(-1);
        } else {
            header = Resp.longToByteArray(payload.length);
            size += 2 + payload.length;
        }
        size += 2 + header.length;

        ByteBuffer o = Resp.buffer(out, size);
        o.put((byte)'$');
        o.put(header);
        if (payload != null) {
            o.put(Resp.CRLF);
            o.put(payload);
        }
        o.put(Resp.CRLF);
    }

    public byte[] raw() {
        return payload;
    }

    @Override
    public Object unwrap() {
        if (payload == null) {
            return null;
        }
        try {
            return new String(payload, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(payload);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof BulkStr) {
            BulkStr bs = (BulkStr)o;
            return Arrays.equals(payload, bs.payload);
        } else {
            return false;
        }
    }
}