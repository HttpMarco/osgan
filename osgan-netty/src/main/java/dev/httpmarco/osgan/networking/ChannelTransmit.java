package dev.httpmarco.osgan.networking;

import io.netty5.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@AllArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(exclude = "id")
public final class ChannelTransmit {

    private String id = "unknown";
    private final Channel channel;

    public <P extends Packet> void sendPacket(P object) {
        channel.writeAndFlush(object);
    }
}