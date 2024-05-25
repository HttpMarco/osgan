package dev.httpmarco.osgan.networking;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@SuppressWarnings("unchecked")
public abstract class AbstractCommunicationComponentBuilder<R extends CommunicationComponent<?>, B extends AbstractCommunicationComponentBuilder<R, ?>> {

    private String hostname = "0.0.0.0";
    private int port = 9090;
    private ChannelConsumer onActive = channel -> {};
    private ChannelConsumer onInactive = channel -> {};


    public B withPort(int port) {
        this.port = port;
        return (B) this;
    }

    public B withHostname(String hostname) {
        this.hostname = hostname;
        return (B) this;
    }

    public B onActive(ChannelConsumer consumer) {
        this.onActive = consumer;
        return (B) this;
    }

    public B onInactive(ChannelConsumer consumer) {
        this.onInactive = consumer;
        return (B) this;
    }

    public abstract R build();

}
