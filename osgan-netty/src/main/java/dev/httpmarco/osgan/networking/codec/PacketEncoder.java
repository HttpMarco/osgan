package dev.httpmarco.osgan.networking.codec;

import dev.httpmarco.osgan.networking.CommunicationListener;
import dev.httpmarco.osgan.networking.packet.Packet;
import dev.httpmarco.osgan.networking.packet.PacketBuffer;
import io.netty5.buffer.Buffer;
import io.netty5.channel.ChannelHandlerContext;
import io.netty5.handler.codec.MessageToByteEncoder;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.logging.Level;

public class PacketEncoder extends MessageToByteEncoder<Packet> {

    private static final HashMap<Packet, PacketBuffer> tempPacketEncoderList = new HashMap<>();

    @Override
    @SneakyThrows
    protected Buffer allocateBuffer(ChannelHandlerContext ctx, Packet packet) {
        try {
            var buffer = PacketBuffer.allocate();

            packet.write(buffer);

            tempPacketEncoderList.put(packet, buffer);

            // amount of chars in class name
            var bytes = Integer.BYTES +
                    // class name
                    packet.getClass().getName().getBytes(StandardCharsets.UTF_8).length +
                    // amount of bytes in buffer
                    Integer.BYTES +
                    // buffer content
                    buffer.getOrigin().readableBytes();

            return ctx.bufferAllocator().allocate(bytes);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet msg, Buffer out) {
        try {
            var origin = tempPacketEncoderList.get(msg).getOrigin();
            var buffer = new PacketBuffer(out);
            var readableBytes = origin.readableBytes();

            buffer.writeString(msg.getClass().getName());
            buffer.writeInt(readableBytes);

            origin.copyInto(0, out, out.writerOffset(), readableBytes);
            out.skipWritableBytes(readableBytes);
        } catch (Exception e) {
            CommunicationListener.getLogger().log(Level.SEVERE, MessageFormat.format("Error while encoding packet {0}", msg.getClass().getName()), e);
        }
        tempPacketEncoderList.remove(msg);
    }
}