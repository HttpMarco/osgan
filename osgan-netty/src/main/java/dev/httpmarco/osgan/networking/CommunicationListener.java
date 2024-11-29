package dev.httpmarco.osgan.networking;

import dev.httpmarco.osgan.networking.channel.ChannelTransmit;
import dev.httpmarco.osgan.networking.packet.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Logger;

@Getter
@Accessors(fluent = true)
public abstract class CommunicationListener {
    private final static Logger LOGGER = Logger.getLogger("osgan-netty");

    private final Map<Class<? extends Packet>, List<BiConsumer<ChannelTransmit, Packet>>> listeners = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <P extends Packet> void listen(Class<P> listeningClass, BiConsumer<ChannelTransmit, P> packetCallback) {
        var packetListeners = listeners.getOrDefault(listeningClass, new ArrayList<>());
        packetListeners.add((BiConsumer<ChannelTransmit, Packet>) packetCallback);
        listeners.put(listeningClass, packetListeners);
    }

    public <P extends Packet> void listen(Class<P> listeningClass, Consumer<P> packetCallback) {
        this.listen(listeningClass, (channelTransmit, packet) -> packetCallback.accept(packet));
    }

    public <P extends Packet> boolean call(@NotNull P packet, ChannelTransmit channelTransmit) {
        if (!this.listeners.containsKey(packet.getClass())) {
            return false;
        }

        for (var consumer : this.listeners.get(packet.getClass())) {
            consumer.accept(channelTransmit, packet);
        }
        return true;
    }

    public abstract void sendPacket(Packet packet);

    //TODO replace with abstract log method to implement in polocloud
    public static Logger getLogger() {
        return LOGGER;
    }
}
