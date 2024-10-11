package dev.httpmarco.osgan.networking;

import dev.httpmarco.osgan.networking.channel.ChannelTransmit;
import dev.httpmarco.osgan.networking.packet.*;
import io.netty5.channel.Channel;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

@Getter
@Accessors(fluent = true)
public abstract class CommunicationListener {

    private final Map<Class<? extends Packet>, List<BiConsumer<ChannelTransmit, Packet>>> listeners = new HashMap<>();
    private final Map<String, Function<CommunicationProperty, Packet>> responders = new HashMap<>();

    private final Map<String, List<ChannelTransmit>> servicesWithResponders = new HashMap<>();
    private final Map<ChannelTransmit, List<String>> respondersByChannel = new HashMap<>();

    private final Map<UUID, PendingRequest> pending = new HashMap<>();
    private final Map<UUID, CommunicationFuture<? extends Packet>> requests = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <P extends Packet> void listen(Class<P> listeningClass, BiConsumer<ChannelTransmit, P> packetCallback) {
        var packetListeners = listeners.getOrDefault(listeningClass, new ArrayList<>());
        packetListeners.add((BiConsumer<ChannelTransmit, Packet>) packetCallback);
        listeners.put(listeningClass, packetListeners);
    }

    public <P extends Packet> void listen(Class<P> listeningClass, Consumer<P> packetCallback) {
        this.listen(listeningClass, (channelTransmit, packet) -> packetCallback.accept(packet));
    }


    public <P extends Packet> CompletableFuture<P> requestAsync(String id, Class<P> packet, CommunicationProperty property) {
        var future = new CommunicationFuture<P>();

        var uuid = UUID.randomUUID();
        this.requests.put(uuid, future);
        sendPacket(new RequestPacket(id, uuid, property));

        return future;
    }

    public <P extends Packet> CompletableFuture<P> requestAsync(String id, Class<P> packet) {
        return this.requestAsync(id, packet, new CommunicationProperty());
    }

    public <P extends Packet> P request(String id, Class<P> packet, CommunicationProperty property) {
        return this.requestAsync(id, packet, property).join();
    }

    public <P extends Packet> P request(String id, Class<P> packet) {
        return this.request(id, packet, new CommunicationProperty());
    }

    public void respond(ChannelTransmit channelTransmit, RequestPacket requestPacket) {
        var response = responders.get(requestPacket.id()).apply(requestPacket.property());
        channelTransmit.sendPacket(new RequestResponsePacket(requestPacket.uuid(), response));
    }

    public boolean hasResponder(String id) {
        return responders.containsKey(id);
    }

    public void responder(String id, Function<CommunicationProperty, Packet> packetFunction) {
        this.responders.put(id, packetFunction);
    }

    public <P extends Packet> boolean call(@NotNull P packet, ChannelTransmit channelTransmit) {
        if (packet instanceof RequestPacket requestPacket) {
            requestReceive(channelTransmit, requestPacket);
            return true;
        }

        if (packet instanceof RequestResponsePacket requestResponsePacket) {
            responseReceive(channelTransmit, requestResponsePacket);
            return true;
        }

        if (packet instanceof BadRequestPacket badRequestPacket) {
            badRequestReceive(channelTransmit, badRequestPacket);
            return true;
        }

        if (packet instanceof RegisterResponderPacket registerResponderPacket) {
            var id = registerResponderPacket.id();

            if (!this.servicesWithResponders.containsKey(id)) {
                this.servicesWithResponders.put(id, new ArrayList<>());
            }

            if (!this.respondersByChannel.containsKey(channelTransmit)) {
                this.respondersByChannel.put(channelTransmit, new ArrayList<>());
            }

            this.servicesWithResponders.get(id).add(channelTransmit);
            this.respondersByChannel.get(channelTransmit).add(id);

            System.out.println("Registered responder: " + id);
            return true;
        }

        if (!this.listeners.containsKey(packet.getClass())) {
            return false;
        }

        for (var consumer : this.listeners.get(packet.getClass())) {
            consumer.accept(channelTransmit, packet);
        }
        return true;
    }

    public abstract void sendPacket(Packet packet);

    public abstract void requestReceive(ChannelTransmit channelTransmit, RequestPacket packet);

    public abstract void badRequestReceive(ChannelTransmit channelTransmit, BadRequestPacket packet);

    public abstract void responseReceive(ChannelTransmit channelTransmit, RequestResponsePacket packet);
}
