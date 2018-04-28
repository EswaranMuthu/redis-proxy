package com.redis.proxy.resp;
import com.redis.proxy.resp.protocol.RespType;

public interface Responses {
    void responseReceived(RespType response);
}