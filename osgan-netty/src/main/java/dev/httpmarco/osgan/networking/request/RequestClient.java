package dev.httpmarco.osgan.networking.request;

import dev.httpmarco.osgan.networking.CommunicationComponent;
import dev.httpmarco.osgan.networking.CommunicationFuture;
import dev.httpmarco.osgan.networking.CommunicationProperty;
import dev.httpmarco.osgan.networking.client.CommunicationClientAction;
import dev.httpmarco.osgan.networking.packet.*;
import dev.httpmarco.osgan.networking.packet.BadRequestPacket;
import dev.httpmarco.osgan.networking.packet.RegisterResponderPacket;
import dev.httpmarco.osgan.networking.packet.RequestPacket;
import dev.httpmarco.osgan.networking.packet.RequestResponsePacket;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@Accessors(fluent = true)
@Getter
public abstract class RequestClient extends CommunicationComponent<CommunicationClientAction> implements Request, Response {
    private final Map<UUID, CommunicationFuture<? extends Packet>> requests = new HashMap<>();
    private final Map<String, Function<CommunicationProperty, Packet>> responders = new HashMap<>();

    public RequestClient(int bossGroupThreads, String hostname, int port) {
        super(bossGroupThreads, hostname, port);

        listen(RequestPacket.class, (transmit, packet) -> {
            if (hasResponder(packet.id())) {
                sendPacket(new RequestResponsePacket(packet.uuid(), buildResponse(packet)));
            }
        });

        listen(BadRequestPacket.class, (transmit, packet) -> {
            completeRequest(packet.uuid(), null);
            this.requests.remove(packet.uuid());
        });

        listen(RequestResponsePacket.class, (transmit, packet) -> {
            completeRequest(packet.uuid(), packet.buildPacket(this.classSupplier()));

            this.requests.remove(packet.uuid());
        });
    }

    @Override
    public void completeRequest(UUID uuid, @Nullable Packet packet) {
        if (hasRequest(uuid)) {
            ((CommunicationFuture<Packet>) this.requests.get(uuid)).complete(packet);
        }
    }

    @Override
    public <P extends Packet> CompletableFuture<P> requestAsync(String id, Class<P> packet, CommunicationProperty property) {
        var future = new CommunicationFuture<P>();
        var uuid = UUID.randomUUID();

        this.requests.put(uuid, future);

        if (this.responders.containsKey(id)) {
            this.completeRequest(uuid, buildResponse(new RequestPacket(id, uuid, property)));
        } else {
            sendPacket(new RequestPacket(id, uuid, property));
        }

        return future;
    }

    @Override
    public void registerResponder(String id, Function<CommunicationProperty, Packet> function) {
        this.responders.put(id, function);
        sendPacket(new RegisterResponderPacket(id));
    }

    @Override
    public void unregisterResponder(String id) {
        this.responders.remove(id);
    }

    @Override
    public Packet buildResponse(RequestPacket requestPacket) {
        return responders.get(requestPacket.id()).apply(requestPacket.property());
    }
}
