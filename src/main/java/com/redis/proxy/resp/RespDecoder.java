package com.redis.proxy.resp;

import com.redis.proxy.resp.protocol.*;
import com.redis.proxy.resp.state.*;
import com.redis.proxy.resp.protocol.*;
import com.redis.proxy.resp.state.*;
import java.nio.ByteBuffer;
import java.util.function.Consumer;

public class RespDecoder {

    private SimpleStrState simpleStrDecoder = new SimpleStrState();
    private ErrState errDecoder = new ErrState();
    private IntState intDecoder = new IntState();
    private BulkStrState bulkStrDecoder;

    private State state = null;

    RespDecoder() {
        bulkStrDecoder = new BulkStrState(this);
    }

    protected void decode(ByteBuffer in, Consumer<RespType> out) {
        while (true) {
            int availableBytes = in.remaining();
            if (availableBytes == 0) {
                //
                // We need more bytes
                //
                break;
            }

            if (state == null) {
                //
                // There is no current state, so read the next byte
                //
                char nextChar = (char) in.get();
                state = nextState(nextChar);
            }
            if (state.decode(in)) {
                out.accept(state.finish());
                state = null;
            } else {
                //
                // We need more bytes
                //
                break;
            }
        }
    }

    public State nextState(char token) {
        switch (token) {
            case '+':
                return simpleStrDecoder.reset();
            case '-':
                return errDecoder.reset();
            case ':':
                return intDecoder.reset();
            case '$':
                return bulkStrDecoder.reset();
            case '*':
                return new AryState(this);
            default:
                throw new IllegalStateException(String.format("Unknown token %s", token));
        }
    }

    public IntState intDecoder() {
        return (IntState)intDecoder.reset();
    }
}