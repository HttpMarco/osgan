package dev.httpmarco.osgan.networking.codec;

import dev.httpmarco.osgan.networking.CommunicationListener;
import dev.httpmarco.osgan.networking.packet.Packet;
import dev.httpmarco.osgan.networking.packet.PacketAllocator;
import dev.httpmarco.osgan.networking.packet.PacketBuffer;
import io.netty5.buffer.Buffer;
import io.netty5.channel.ChannelHandlerContext;
import io.netty5.handler.codec.ByteToMessageDecoder;
import lombok.RequiredArgsConstructor;

import java.text.MessageFormat;
import java.util.logging.Level;

@RequiredArgsConstructor
public class PacketDecoder extends ByteToMessageDecoder {
    private final CommunicationListener listener;

    @Override
    protected void decode(ChannelHandlerContext ctx, Buffer in) {
        var buffer = new PacketBuffer(in);
        var className = buffer.readString();

        try {
            var readableBytes = buffer.readInt();
            var content = new PacketBuffer(in.copy(in.readerOffset(), readableBytes, true));
            in.skipReadableBytes(readableBytes);

            var packet = (Packet) PacketAllocator.allocate(Class.forName(className));

            packet.read(content);

            buffer.resetBuffer();
            ctx.fireChannelRead(packet);
        } catch (Exception e) {
            CommunicationListener.getLogger().log(Level.SEVERE, MessageFormat.format("Error while decoding packet! Could not find packet class: {0}", className), e);
        }
    }
}