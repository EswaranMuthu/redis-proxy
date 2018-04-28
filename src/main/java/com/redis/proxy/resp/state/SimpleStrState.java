package com.redis.proxy.resp.state;

import com.redis.proxy.resp.protocol.SimpleStr;
import com.redis.proxy.resp.protocol.RespType;

public class SimpleStrState extends ScannableState {
    @Override
    public RespType finish() {
        return new SimpleStr(bufferAsString());
    }
}