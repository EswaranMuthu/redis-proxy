package com.redis.proxy.resp;

import java.io.IOException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.*;
import org.springframework.stereotype.*;

@Component
public class ConnectionGroup extends Thread {
    private static int threadId = 1;

    private Map<Integer, Connection> connections = Collections.synchronizedMap(new HashMap<>());
    private final Selector selector;

    private boolean shutdown = false;


    ConnectionGroup() throws IOException {
        selector = Selector.open();

        setName(String.format("ConnectionGroup-%d", threadId++));
        setDaemon(true);
    }

    SelectionKey add(Connection con) throws ClosedChannelException {
        connections.put(con.id, con);
        return con.channel.register(selector, SelectionKey.OP_READ, con.id);
    }

    void remove(Connection con) {
        connections.remove(con.id);
    }

    public void run() {
        while (!shutdown) {
            try {
                selector.select(100);
                Set<SelectionKey> keys = selector.selectedKeys();
                for (SelectionKey key : keys) {
                    Connection connection = connections.get(key.attachment());
                    try {
                        if (key.isReadable()) {
                            connection.readTick();
                        }
                        if (key.isWritable()) {
                            connection.writeTick();
                        }
                    } catch (IOException e) {
                        connection.reportException(e);
                        connection.stop();
                    } catch (CancelledKeyException e) {
                        // The key may have been cancelled in the meantime
                        connection.stop();
                    }
                }
            } catch (IOException e) {
                shutdownBecause(e);
            }
        }
    }

    private void shutdownBecause(Exception t) {
        shutdown = true;
        List<Exception> failures = new ArrayList<>();

        connections.values().forEach(con -> {
            con.reportException(t);
            try {
                con.shutdown();
            } catch (IOException e) {
                failures.add(e);
            }
        });

        if (!failures.isEmpty()) {
            throw new RuntimeException("Multiple failures shutting down: " + failures);
        }
    }

    public void shutdown() {
        shutdown = true;
        List<Exception> failures = new ArrayList<>();

        connections.values().forEach(con -> {
            try {
                con.shutdown();
            } catch (IOException e) {
                failures.add(e);
            }
        });

        if (!failures.isEmpty()) {
            throw new RuntimeException("Multiple failures shutting down: " + failures);
        }
    }
}