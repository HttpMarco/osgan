package dev.httpmarco.osgan.networking.channel;

import dev.httpmarco.osgan.networking.CommunicationListener;
import dev.httpmarco.osgan.networking.packet.Packet;
import io.netty5.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.log4j.Log4j2;

import java.text.MessageFormat;
import java.util.logging.Level;

@Getter
@AllArgsConstructor
@Accessors(fluent = true)
public class ChannelTransmit extends CommunicationListener {

    private final Channel channel;

    @Override
    public void sendPacket(Packet packet) {
        if (channel == null) {
            CommunicationListener.getLogger().log(Level.SEVERE, MessageFormat.format("Write packet {0} on channel failed, channel is null", packet.getClass().getSimpleName()));
            return;
        }
        this.channel.writeAndFlush(packet);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ChannelTransmit transmit && transmit.channel.equals(this.channel);
    }
}
