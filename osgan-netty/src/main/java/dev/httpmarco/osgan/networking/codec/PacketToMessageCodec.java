package dev.httpmarco.osgan.networking.codec;

import dev.httpmarco.osgan.networking.Packet;
import dev.httpmarco.osgan.reflections.Reflections;
import io.netty5.channel.ChannelHandlerContext;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

public class PacketToMessageCodec extends AbstractMessageToPacket {

    @Override
    public void encode(ChannelHandlerContext ctx, Packet msg, @NotNull CodecBuffer buffer) {
        buffer.writeString(msg.getClass().getName());
    }

    @SneakyThrows
    @Override
    public void decode(@NotNull ChannelHandlerContext ctx, @NotNull CodecBuffer buffer) {
        var clazz = Class.forName(buffer.readString());

        var packet = new Reflections<>(clazz).allocate();


        buffer.resetBuffer();
        ctx.fireChannelRead(packet);
    }
}
