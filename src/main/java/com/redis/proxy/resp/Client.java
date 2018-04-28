package com.redis.proxy.resp;

import java.io.IOException;
import javax.annotation.*;
import lombok.*;
import lombok.extern.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.*;

/**
 * The owner of one-or-more connections.
 */
@Slf4j
public class Client {
    private final String hostname;

    private final int port;

    private String password;
    private Integer db;

    private final ConnectionGroup group;

    public Client(String hostname, int port) throws IOException {
        this.hostname = hostname;
        this.port = port;

        group = new ConnectionGroup();
        group.start();
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setDb(Integer db) {
        this.db = db;
    }

    public Connection makeConnection() throws IOException {
        Connection con = new Connection(hostname, port, group);
        con.setPassword(password);
        con.setDb(db);
        return con;
    }

    public void shutdown() throws IOException {
        group.shutdown();
    }
}