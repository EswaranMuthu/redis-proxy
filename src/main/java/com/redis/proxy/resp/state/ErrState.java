package com.redis.proxy.resp.state;

import com.redis.proxy.resp.protocol.RespType;
import com.redis.proxy.resp.protocol.Err;

public class ErrState extends ScannableState {
    @Override
    public RespType finish() {
        return new Err(bufferAsString());
    }
}