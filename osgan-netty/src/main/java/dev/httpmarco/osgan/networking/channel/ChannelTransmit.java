package dev.httpmarco.osgan.networking.channel;

import dev.httpmarco.osgan.networking.CommunicationListener;
import dev.httpmarco.osgan.networking.packet.BadRequestPacket;
import dev.httpmarco.osgan.networking.packet.Packet;
import dev.httpmarco.osgan.networking.packet.RequestPacket;
import dev.httpmarco.osgan.networking.packet.RequestResponsePacket;
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
    public void requestReceive(ChannelTransmit channelTransmit, RequestPacket packet) {
    }

    @Override
    public void badRequestReceive(ChannelTransmit channelTransmit, BadRequestPacket packet) {
    }

    @Override
    public void responseReceive(ChannelTransmit channelTransmit, RequestResponsePacket packet) {
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ChannelTransmit transmit && transmit.channel.equals(this.channel);
    }
}
