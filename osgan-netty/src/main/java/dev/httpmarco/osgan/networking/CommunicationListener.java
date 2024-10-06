package dev.httpmarco.osgan.networking;

import dev.httpmarco.osgan.networking.channel.ChannelTransmit;
import dev.httpmarco.osgan.networking.packet.BadRequestPacket;
import dev.httpmarco.osgan.networking.packet.Packet;
import dev.httpmarco.osgan.networking.packet.RequestPacket;
import dev.httpmarco.osgan.networking.packet.RequestResponsePacket;
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

    @Deprecated
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


    public void callResponder(ChannelTransmit channelTransmit, @NotNull RequestPacket requestPacket) {
        if (!responders.containsKey(requestPacket.id())) {
            channelTransmit.sendPacket(new BadRequestPacket(requestPacket.uuid()));
            System.out.println("Found no responder for: " + requestPacket.id());
            return;
        }

        var response = responders.get(requestPacket.id()).apply(requestPacket.property());
        channelTransmit.sendPacket(new RequestResponsePacket(requestPacket.uuid(), response));
    }

    public void responder(String id, Function<CommunicationProperty, Packet> packetFunction) {
        this.responders.put(id, packetFunction);
    }

    public <P extends Packet> boolean call(@NotNull P packet, ChannelTransmit channelTransmit) {
        if (packet instanceof RequestPacket requestPacket) {
            this.callResponder(channelTransmit, requestPacket);
            return true;
        }

        if (packet instanceof RequestResponsePacket requestResponsePacket) {
            if (!this.requests.containsKey(requestResponsePacket.uuid())) {
                return true;
            }
            ((CommunicationFuture<Packet>) this.requests.get(requestResponsePacket.uuid())).complete(requestResponsePacket.response());
            this.requests.remove(requestResponsePacket.uuid());
            return true;
        }

        if (packet instanceof BadRequestPacket badRequestPacket) {
            this.requests.remove(badRequestPacket.uuid());
            System.out.println("Invalid request from: " + badRequestPacket.uuid());
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
}
