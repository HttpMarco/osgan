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

    private final Channel channel;

    @Override
    public void sendPacket(Packet packet) {
        if (channel == null) {
            System.err.println("Write packet " + packet.getClass().getSimpleName() + " on channel failed, channel is null");
            return;
        }
        this.channel.writeAndFlush(packet);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ChannelTransmit transmit && transmit.channel.equals(this.channel);
    }
}
