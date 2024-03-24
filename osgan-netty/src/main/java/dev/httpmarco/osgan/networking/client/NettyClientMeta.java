package dev.httpmarco.osgan.networking.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public class NettyClientMeta {

    // connection host address
    private String hostname;
    // connection port
    private int port;

    // if connection is not present, time for reconnect scheduling
    private long reconnectSchedule;
    // time for wait a successful connection response
    private int connectionTimeout;

    private boolean hasReconnection() {
        return this.reconnectSchedule != -1;
    }
}