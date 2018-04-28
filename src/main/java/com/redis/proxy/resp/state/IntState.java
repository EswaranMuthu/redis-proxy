package com.redis.proxy.resp.state;
import com.redis.proxy.resp.protocol.RespType;
import com.redis.proxy.resp.protocol.Int;

public class IntState extends ScannableState {
    @Override
    public RespType finish() {
        return new Int(finishInt());
    }

    long finishInt() {
        return Long.parseLong(bufferAsString());
    }
}