package dev.httpmarco.osgan.networking;

@FunctionalInterface
public interface ChannelConsumer {

    void listen(ChannelTransmit channel);

}
