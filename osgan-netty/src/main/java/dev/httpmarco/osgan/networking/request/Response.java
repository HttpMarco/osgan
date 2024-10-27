package dev.httpmarco.osgan.networking.request;

import dev.httpmarco.osgan.networking.CommunicationProperty;
import dev.httpmarco.osgan.networking.packet.Packet;
import dev.httpmarco.osgan.networking.packet.RequestPacket;

import java.util.Map;
import java.util.function.Function;

public interface Response {
    void registerResponder(String id, Function<CommunicationProperty, Packet> function);

    void unregisterResponder(String id);

    Packet buildResponse(RequestPacket requestPacket);

    Map<String, Function<CommunicationProperty, Packet>> responders();

    default boolean hasResponder(String id) {
        return responders().containsKey(id);
    }
}
