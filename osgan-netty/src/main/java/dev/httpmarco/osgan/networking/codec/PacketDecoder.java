package dev.httpmarco.osgan.networking.codec;

import dev.httpmarco.osgan.networking.Packet;
import dev.httpmarco.osgan.reflections.common.Allocator;
import io.netty5.buffer.Buffer;
import io.netty5.channel.ChannelHandlerContext;
import io.netty5.handler.codec.ByteToMessageDecoder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class PacketDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, Buffer in) {
        var buffer = new CodecBuffer(in);

        var className = buffer.readString();

        try {
            var readableBytes = buffer.readInt();
            var content = new CodecBuffer(in.copy(in.readerOffset(), readableBytes, true));
            in.skipReadableBytes(readableBytes);

            Packet packet = (Packet) Allocator.allocate(Class.forName(className));

            packet.onRead(content);

            buffer.resetBuffer();

            ctx.fireChannelRead(packet);
        } catch (Exception e) {
            System.err.println("Error while decoding packet " + className);
            e.printStackTrace();
        }
    }
}
