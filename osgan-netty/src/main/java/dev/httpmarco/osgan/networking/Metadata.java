package dev.httpmarco.osgan.networking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
public abstract class Metadata {

    // connection host address
    private String hostname;
    // connection port
    private int port;
    // called when a channel is active
    private ChannelConsumer onActive;
    // called when a channel is inactive
    private ChannelConsumer onInactive;

}
