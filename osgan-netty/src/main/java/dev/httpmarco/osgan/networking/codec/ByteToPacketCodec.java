package dev.httpmarco.osgan.networking.codec;

import dev.httpmarco.osgan.networking.packet.Packet;
import io.netty5.buffer.Buffer;
import io.netty5.channel.ChannelHandlerContext;
import io.netty5.handler.codec.ByteToMessageCodec;
import io.netty5.handler.codec.CodecException;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public final class ByteToPacketCodec extends ByteToMessageCodec<Packet> {

    private static final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

    @Override
    public void encode(ChannelHandlerContext ctx, Packet msg, Buffer buffer) {
        try (var objectStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectStream.writeObject(msg);
        } catch (Exception e) {
            throw new CodecException(e);
        }
    }

    @Override
    public void decode(ChannelHandlerContext ctx, @NotNull Buffer buffer) {
        var bytes = new byte[buffer.readableBytes()];

        for (var i = 0; i < bytes.length; i++) {
            bytes[i] = buffer.readByte();
        }

        try (var byteStream2 = new ByteArrayInputStream(bytes)) {
            ctx.fireChannelRead(new ObjectInputStream(byteStream2));
        } catch (Exception e) {
            throw new CodecException(e);
        }
    }
}
