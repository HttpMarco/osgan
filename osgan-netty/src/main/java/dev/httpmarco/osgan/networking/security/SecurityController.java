package dev.httpmarco.osgan.networking.security;

import dev.httpmarco.osgan.networking.channel.ChannelTransmit;
import lombok.Getter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
public abstract class SecurityController {

    private final List<ChannelTransmit> verifiedConnections = new CopyOnWriteArrayList<>();

    public abstract boolean handleFirstConnection(ChannelTransmit transmit);

    public abstract void unauthorize(ChannelTransmit transmit);

    public abstract void authorize(ChannelTransmit transmit);

}
