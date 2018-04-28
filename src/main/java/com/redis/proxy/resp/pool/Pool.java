package com.redis.proxy.resp.pool;

import com.redis.proxy.resp.*;
import com.redis.proxy.resp.protocol.*;
import java.io.*;
import java.util.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;

/**
 * A connection-pool for JRESP connections.  Since JRESP is asynchronous, most operations can be multiplexed onto one
 * single shared connection.  However there are some Redis commands that are exceptions to this rule.
 */
public class Pool {
    private Client client;

    private SingleCommandConnection shared;

    private Set<SingleCommandConnection> borrowable = new HashSet<>();
    private Set<SingleCommandConnection> borrowed = new HashSet<>();
    public static Pool pool;
    private Pool(Client client) {
        this.client = client;
    }

    public static Pool poolFactory(Client client){
        if(pool == null) {
            synchronized (Pool.class) {
                if (pool == null) {
                    pool = new Pool(client);
                    return pool;
                }
            }
        }
        return pool;
    }

    public String toString() {
        return String.format("%s[client=%s,shared=%s,pubSub=%s,borrowable=%s,borrowed=%s]",
                getClass().getName(),
                client,
                shared,
                borrowable,
                borrowed);
    }

    /**
     * The shared connection is for the majority of Redis commands that return one single response.  JRESP will
     * automatically pipeline such commands for efficiency.
     *
     * Do not use such a connection for any blocking, pub-sub, or any other command that doesn't return one single
     * response.
     *
     * Because this is shared, the connection will be started.
     */
    public synchronized SingleCommandConnection getShared() throws IOException, ConnectionException {
        if ((shared == null) || (shared.isShutdown())) {
            shared = new SingleCommandConnection(client.makeConnection());
        }

        return shared;
    }

    /**
     * A dedicated connection is one that is not shared, and not reused.  It is used for commands that permanently
     * change the nature of the connection - e.g. MONITOR.
     *
     * The returned connection is not started.  The caller must start it as appropriate.
     */
    public Connection getDedicated() throws IOException {
        return client.makeConnection();
    }

    /**
     * A borrowed connection is one that a borrower has exclusive use of until it is returned.  The borrower must
     * return it to avoid any leaks.  It is used mainly for blocking commands like BLPOP.
     */
    public synchronized SingleCommandConnection getBorrowed() throws IOException, ConnectionException {
        Iterator<SingleCommandConnection> i = borrowable.iterator();
        while (i.hasNext()) {
            SingleCommandConnection con = i.next();
            i.remove();

            if (!con.isShutdown()) {
                borrowed.add(con);
                return con;
            }
        }

        SingleCommandConnection con = new SingleCommandConnection(client.makeConnection());
        borrowed.add(con);
        return con;
    }

    /**
     * Return a borrowed connection
     */
    public synchronized void returnBorrowed(SingleCommandConnection con) {
        if (borrowed.remove(con)) {
            borrowable.add(con);
        } else {
            throw new IllegalStateException("This connection was not previously borrowed");
        }
    }

    /**
     * Shutdown a pool and it's underlying Client.  No connections issued by this pool will work once this has been
     * shutdown.
     */
    public void shutdown() throws IOException {
        client.shutdown();
    }
}