package dev.httpmarco.osgan.networking.request;

import dev.httpmarco.osgan.files.json.JsonObjectSerializer;
import dev.httpmarco.osgan.files.json.JsonUtils;
import dev.httpmarco.osgan.networking.CommunicationComponent;
import dev.httpmarco.osgan.networking.Packet;
import dev.httpmarco.osgan.networking.request.packets.RegisterResponderPacket;
import dev.httpmarco.osgan.networking.request.packets.RequestPacket;
import dev.httpmarco.osgan.networking.server.NettyServer;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class RequestHandler {
    private final Map<String, PacketResponder<?>> responders = new HashMap<>();

    private final Map<UUID, Consumer<Packet>> requests = new HashMap<>();
    private final Map<UUID, Class<? extends Packet>> requestClass = new HashMap<>();

    private final CommunicationComponent<?> component;

    public <T extends Packet> void request(String id, Class<T> responsePacket, Consumer<T> consumer) {
        this.request(id, new JsonObjectSerializer(), responsePacket, consumer);
    }

    @SuppressWarnings("unchecked")
    public <T extends Packet> void request(String id, JsonObjectSerializer properties, Class<T> responsePacket, Consumer<T> consumer) {
        var uniqueId = UUID.randomUUID();

        this.component.sendPacket(new RequestPacket(id, uniqueId, properties));

        this.requests.put(uniqueId, (Consumer<Packet>) consumer);
        this.requestClass.put(uniqueId, responsePacket);
    }

    public void acceptRequest(UUID uniqueId, String responseJson) {
        this.requests.get(uniqueId).accept(JsonUtils.fromJson(responseJson, this.requestClass.get(uniqueId)));

        this.removeRequest(uniqueId);
    }

    public void removeRequest(UUID uniqueId) {
        this.requests.remove(uniqueId);
        this.requestClass.remove(uniqueId);
    }

    public boolean isRequestPresent(UUID uniqueId) {
        return this.requests.containsKey(uniqueId);
    }

    public <T extends Packet> void registerResponder(String id, PacketResponder<T> responder) {
        this.responders.put(id, responder);

        if (this.component instanceof NettyServer) {
            this.component.sendPacket(new RegisterResponderPacket(id));
        }
    }

    public PacketResponder<?> getResponder(String id) {
        return this.responders.get(id);
    }

    public boolean isResponderPresent(String id) {
        return this.responders.containsKey(id);
    }
}
