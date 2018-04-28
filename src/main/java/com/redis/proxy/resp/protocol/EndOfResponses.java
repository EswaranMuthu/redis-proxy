package com.redis.proxy.resp.protocol;

import java.nio.ByteBuffer;
import java.util.Deque;

/**
 * Not an official part of the RESP spec, used to indicate that no more responses will be sent in this context.
 *
 * e.g. after unsubscribing to something that was previously subscribed
 */
public class EndOfResponses implements RespType {

    @Override
    public void writeBytes(Deque<ByteBuffer> out) {
        throw new UnsupportedOperationException("Should not be sent anywhere");
    }

    @Override
    public Object unwrap() {
        return null;
    }
}