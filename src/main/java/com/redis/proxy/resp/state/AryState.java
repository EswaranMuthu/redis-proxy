package com.redis.proxy.resp.state;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import com.redis.proxy.resp.RespDecoder;
import com.redis.proxy.resp.protocol.RespType;
import com.redis.proxy.resp.protocol.Ary;

public class AryState implements State {
    private RespDecoder parent;

    private IntState intState;
    private Integer aryLength = null;
    private List<RespType> ary = null;
    private State nextState = null;

    public AryState(RespDecoder parent) {
        this.parent = parent;
        this.intState = parent.intDecoder();
    }

    @Override
    public boolean decode(ByteBuffer in) {
        while (true) {
            if (aryLength == null) {
                if (intState.decode(in)) {
                    aryLength = (int) intState.finishInt();
                    ary = new ArrayList<>(aryLength);
                    if (aryLength == 0) {
                        //
                        // This is an empty array, so there will be no "nextState", so let's short-cut proceedings here
                        //
                        return true;
                    }
                }
            }
            if (in.remaining() == 0) {
                return false;
            }
            if (nextState == null) {
                nextState = parent.nextState((char) in.get());
            }
            if (nextState.decode(in)) {
                ary.add(nextState.finish());
                if (ary.size() == aryLength) {
                    return true;
                } else {
                    nextState = null;
                    // Lets go around again, there's (probably more to do).
                }
            } else {
                return false;
            }
        }
    }

    @Override
    public RespType finish() {
        return new Ary(ary);
    }
}