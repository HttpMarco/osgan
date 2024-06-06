package dev.httpmarco.osgan.networking.channel;

import dev.httpmarco.osgan.networking.CommunicationListener;
import dev.httpmarco.osgan.networking.packet.Packet;
import io.netty5.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

@Getter
@AllArgsConstructor
@Accessors(fluent = true)
public class ChannelTransmit extends CommunicationListener {

    private final String id;
    private final Channel channel;

    @Override
    public void sendPacket(Packet packet) {
        this.channel.writeAndFlush(packet);
    }
}
