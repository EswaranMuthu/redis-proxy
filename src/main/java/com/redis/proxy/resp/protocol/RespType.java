package com.redis.proxy.resp.protocol;

import java.nio.ByteBuffer;
import java.util.Deque;

/**
 * Defines the five types implemented by the RESP protocol
 */
public interface RespType {
    /**
     * Write the RESP form to the ByteBuf
     */
    void writeBytes(Deque<ByteBuffer> out);

    /**
     * Return the high-level Java equivalent.
     */
    Object unwrap();
}