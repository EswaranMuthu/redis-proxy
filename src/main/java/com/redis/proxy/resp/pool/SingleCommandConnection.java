package com.redis.proxy.resp.pool;


import com.redis.proxy.resp.*;
import com.redis.proxy.resp.protocol.*;
import java.io.*;
import java.util.*;
import org.springframework.stereotype.*;

/**
 * A connection used solely for commands that have a single response to each request
 */
public class SingleCommandConnection {
    private Connection connection;

    private final Deque<Responses> responseQueue = new ArrayDeque<>();

    public SingleCommandConnection(Connection connection) throws IOException, ConnectionException {
        this.connection = connection;
        this.connection.start(this::dispatcher);
    }

    private void dispatcher(RespType resp) {
        if (resp instanceof EndOfResponses) {
            synchronized (responseQueue) {
                responseQueue.forEach(responder -> responder.responseReceived(resp));
            }
        } else {
            Responses respondTo = null;
            synchronized (responseQueue) {
                if (responseQueue.isEmpty()) {
                    if (resp instanceof ClientErr) {
                        // There are no waiting responses, so nowhere to send the response to.
                    } else {
                        // There is a response but nowhere to send it to.
                        throw new IllegalStateException("Got an unexpected response: " + resp);
                    }
                } else {
                    respondTo = responseQueue.pop();
                }
            }
            if (respondTo != null) {
                respondTo.responseReceived(resp);
            }
        }
    }

    public void write(RespType command, Responses responses) {
        synchronized (responseQueue) {
            responseQueue.add(responses);
            connection.write(command);
        }
    }

    public boolean isShutdown() {
        return connection.isShutdown();
    }
}