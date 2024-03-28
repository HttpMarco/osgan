package dev.httpmarco.osgan.networking.request;

import dev.httpmarco.osgan.networking.ChannelTransmit;
import io.netty5.channel.Channel;

import java.util.UUID;

public record PendingRequest(ChannelTransmit transmit, String id, UUID uniqueId, long timestamp) {
}
