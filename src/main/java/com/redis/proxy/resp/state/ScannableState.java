package com.redis.proxy.resp.state;


import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import com.redis.proxy.resp.protocol.Resp;

abstract class ScannableState implements State {
    private static final int DEFAULT_SIZE = 32;
    private static final int MAXIMUM_SIZE = 32 * 1000;
    private byte[] buffer = new byte[DEFAULT_SIZE];
    private int idx = 0;

    public ScannableState reset() {
        if (buffer.length > MAXIMUM_SIZE) {
            buffer = new byte[DEFAULT_SIZE];
        }
        idx = 0;
        return this;
    }

    @Override
    public boolean decode(ByteBuffer in) {
        int available = in.remaining();
        if (available > 0) {
            for (int i = 0; i < available; i++) {
                byte b = in.get();
                if (idx > 0 && buffer[idx - 1] == Resp.CRLF[0] && b == Resp.CRLF[1]) {
                    return true;
                } else {
                    if (idx == buffer.length) {
                        resize();
                    }
                    buffer[idx++] = b;
                }
            }
        }

        return false;
    }

    private void resize() {
        byte[] newBuffer = new byte[buffer.length * 2];
        System.arraycopy(buffer, 0, newBuffer, 0, buffer.length);
        buffer = newBuffer;
    }

    protected String bufferAsString() {
        return new String(buffer, 0, idx - 1, Charset.forName("UTF-8"));
    }
}