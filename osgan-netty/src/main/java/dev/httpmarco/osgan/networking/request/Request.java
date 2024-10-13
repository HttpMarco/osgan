package dev.httpmarco.osgan.networking.request;

import dev.httpmarco.osgan.networking.CommunicationFuture;
import dev.httpmarco.osgan.networking.CommunicationProperty;
import dev.httpmarco.osgan.networking.packet.Packet;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface Request {
    Map<UUID, CommunicationFuture<? extends Packet>> requests();

    void completeRequest(UUID uuid, Packet packet);

    <P extends Packet> CompletableFuture<P> requestAsync(String id, Class<P> packet, CommunicationProperty property);

    default <P extends Packet> CompletableFuture<P> requestAsync(String id, Class<P> packet) {
        return this.requestAsync(id, packet, new CommunicationProperty());
    }

    default <P extends Packet> P request(String id, Class<P> packet, CommunicationProperty property) {
        return this.requestAsync(id, packet, property).join();
    }

    default <P extends Packet> P request(String id, Class<P> packet) {
        return this.request(id, packet, new CommunicationProperty());
    }

    default boolean hasRequest(UUID uuid) {
        return requests().containsKey(uuid);
    }
}
