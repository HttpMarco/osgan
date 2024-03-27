package dev.httpmarco.osgan.networking.codec;

import dev.httpmarco.osgan.networking.Packet;
import io.netty5.buffer.Buffer;
import io.netty5.channel.ChannelHandlerContext;
import io.netty5.handler.codec.ByteToMessageCodec;

public abstract class AbstractMessageToPacket extends ByteToMessageCodec<Packet> {

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet msg, Buffer out) throws Exception {
        this.encode(ctx, msg, new CodecBuffer(out));
    }

    protected void decode(ChannelHandlerContext ctx, Buffer buffer) throws Exception {
        this.decode(ctx, new CodecBuffer(buffer));
    }

    public abstract void encode(ChannelHandlerContext ctx, Packet msg, CodecBuffer buffer);

    public abstract void decode(ChannelHandlerContext ctx, CodecBuffer buffer);
}
