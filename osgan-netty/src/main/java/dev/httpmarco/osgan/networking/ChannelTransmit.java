package dev.httpmarco.osgan.networking;

import io.netty5.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Setter
@Accessors(fluent = true)
@AllArgsConstructor
public final class ChannelTransmit {

    private Channel channel;

    public <P extends Packet> void sendPacket(P object) {
        channel.writeAndFlush(object);
    }
}