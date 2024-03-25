package dev.httpmarco.osgan.networking;

import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
public abstract class AbstractCommunicationComponentBuilder<R extends CommunicationComponent<?>, B extends AbstractCommunicationComponentBuilder<R, ?>> {

    private String hostname = "0.0.0.0";
    private int port = 9090;

    public B withPort(int port) {
        this.port = port;
        return (B) this;
    }

    public B withHostname(String hostname) {
        this.hostname = hostname;
        return (B) this;
    }

    public abstract R build();

}
