package dev.httpmarco.osgan.networking.client.metadata;

import dev.httpmarco.osgan.networking.client.NettyClient;
import dev.httpmarco.osgan.networking.metadata.Metadata;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

@Getter
@Accessors(fluent = true)
public final class NettyClientMeta extends Metadata {

    // if connection is not present, time for reconnect scheduling
    private final long reconnectSchedule;
    // time for wait a successful connection response
    private final int connectionTimeout;

    private final Map<NettyClient.Event, List<Runnable>> clientEvents;

    public NettyClientMeta(String hostname, int port, long reconnectSchedule, int connectionTimeout, Map<NettyClient.Event, List<Runnable>> clientEvents) {
        super(hostname, port);
        this.reconnectSchedule = reconnectSchedule;
        this.connectionTimeout = connectionTimeout;
        this.clientEvents = clientEvents;
    }


    public boolean hasReconnection() {
        return this.reconnectSchedule != -1;
    }
}