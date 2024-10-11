package dev.httpmarco.osgan.networking.packet;

import dev.httpmarco.osgan.networking.channel.ChannelTransmit;

import java.util.UUID;

public record PendingRequest(ChannelTransmit transmit, String id, UUID uniqueId, long timestamp) {
}
