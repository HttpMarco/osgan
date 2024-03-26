package dev.httpmarco.osgan.networking.codec;

import dev.httpmarco.osgan.networking.buffer.PacketBuffer;
import dev.httpmarco.osgan.networking.packet.Packet;
import io.netty5.buffer.Buffer;
import io.netty5.channel.ChannelHandlerContext;
import io.netty5.handler.codec.ByteToMessageCodec;

abstract class AbstractByteToPacketBufferCodec extends ByteToMessageCodec<Packet> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet msg, Buffer out) {
        this.encode(ctx, msg, new PacketBuffer(out));
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, Buffer in) {
        this.decode(ctx, new PacketBuffer(in));
    }

    public abstract void encode(ChannelHandlerContext ctx, Packet msg, PacketBuffer buffer);

    public abstract void decode(ChannelHandlerContext ctx, PacketBuffer buffer);

}
