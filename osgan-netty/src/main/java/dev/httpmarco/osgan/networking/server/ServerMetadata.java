package dev.httpmarco.osgan.networking.server;

import dev.httpmarco.osgan.networking.ChannelConsumer;
import dev.httpmarco.osgan.networking.Metadata;

public final class ServerMetadata extends Metadata {

    public ServerMetadata(String hostname, int port, ChannelConsumer onActive, ChannelConsumer onInactive) {
        super(hostname, port, onActive, onInactive);
    }
}
