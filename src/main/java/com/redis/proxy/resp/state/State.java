package com.redis.proxy.resp.state;

import com.redis.proxy.resp.protocol.RespType;
import java.nio.ByteBuffer;

public interface State {
    boolean decode(ByteBuffer in);
    RespType finish();
}