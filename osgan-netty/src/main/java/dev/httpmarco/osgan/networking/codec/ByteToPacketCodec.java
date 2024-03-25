package dev.httpmarco.osgan.networking.codec;

import dev.httpmarco.osgan.networking.packet.Packet;
import io.netty5.buffer.Buffer;
import io.netty5.channel.ChannelHandlerContext;
import io.netty5.handler.codec.ByteToMessageCodec;

public final class ByteToPacketCodec extends ByteToMessageCodec<Packet> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet msg, Buffer out) throws Exception {

    }

    @Override
    protected void decode(ChannelHandlerContext ctx, Buffer in) throws Exception {

    }
}
