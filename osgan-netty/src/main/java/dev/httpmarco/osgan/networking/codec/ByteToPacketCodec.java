package dev.httpmarco.osgan.networking.codec;

import dev.httpmarco.osgan.networking.buffer.PacketBuffer;
import dev.httpmarco.osgan.networking.packet.Packet;
import io.netty5.channel.ChannelHandlerContext;

public final class ByteToPacketCodec extends AbstractByteToPacketBufferCodec {

    @Override
    public void encode(ChannelHandlerContext ctx, Packet msg, PacketBuffer buffer) {

    }

    @Override
    public void decode(ChannelHandlerContext ctx, PacketBuffer buffer) {

    }
}
