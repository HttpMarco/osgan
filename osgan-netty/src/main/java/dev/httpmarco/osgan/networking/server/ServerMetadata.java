package dev.httpmarco.osgan.networking.server;

import dev.httpmarco.osgan.networking.metadata.Metadata;

public final class ServerMetadata extends Metadata {

    public ServerMetadata(String hostname, int port) {
        super(hostname, port);
    }
}
