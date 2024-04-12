package dev.httpmarco.osgan.networking.server;

import dev.httpmarco.osgan.networking.AbstractCommunicationComponentBuilder;

public final class NettyServerBuilder extends AbstractCommunicationComponentBuilder<NettyServer, NettyServerBuilder> {

    @Override
    public NettyServer build() {
        return new NettyServer(new ServerMetadata(hostname(), port(), onActive(), onInactive()));
    }
}
