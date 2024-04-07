package dev.httpmarco.osgan.networking.client;

import dev.httpmarco.osgan.networking.Metadata;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;

@Getter
@Accessors(fluent = true)
public final class ClientMetadata extends Metadata {

    private final @Nullable String id;
    // if connection is not present, time for reconnect scheduling
    private final long reconnectSchedule;
    // time for wait a successful connection response
    private final int connectionTimeout;

    public ClientMetadata(@Nullable String id, String hostname, int port, long reconnectSchedule, int connectionTimeout) {
        super(hostname, port);
        this.id = id;
        this.reconnectSchedule = reconnectSchedule;
        this.connectionTimeout = connectionTimeout;
    }

    public boolean hasReconnection() {
        return this.reconnectSchedule != -1;
    }
}