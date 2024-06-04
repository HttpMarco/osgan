package dev.httpmarco.osgan.networking.codec;

import dev.httpmarco.osgan.networking.packet.Packet;
import dev.httpmarco.osgan.networking.packet.PacketBuffer;
import dev.httpmarco.osgan.reflections.Reflections;
import dev.httpmarco.osgan.reflections.common.Allocator;
import io.netty5.buffer.Buffer;
import io.netty5.channel.ChannelHandlerContext;
import io.netty5.handler.codec.ByteToMessageDecoder;
import lombok.SneakyThrows;

import java.lang.reflect.Modifier;
import java.sql.Ref;
import java.util.UUID;

public class PacketDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, Buffer in) {
        var buffer = new PacketBuffer(in);

        var className = buffer.readString();

        try {
            var readableBytes = buffer.readInt();
            var content = new PacketBuffer(in.copy(in.readerOffset(), readableBytes, true));
            in.skipReadableBytes(readableBytes);

            var packet = (Packet) Allocator.allocate(Class.forName(className));

            packet.read(content);

            buffer.resetBuffer();
            ctx.fireChannelRead(packet);
        } catch (Exception e) {
            System.err.println("Error while decoding packet " + className);
            e.printStackTrace();
        }
    }
}