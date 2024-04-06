package dev.httpmarco.osgan.networking.codec;

import dev.httpmarco.osgan.networking.Packet;
import io.netty5.buffer.Buffer;
import io.netty5.channel.ChannelHandlerContext;
import io.netty5.handler.codec.ByteToMessageDecoder;

public class PacketDecoder extends ByteToMessageDecoder {
    @SuppressWarnings("unchecked")
    @Override
    protected void decode(ChannelHandlerContext ctx, Buffer in) {
        var buffer = new CodecBuffer(in);

        var className = buffer.readString();

        try {
            var readableBytes = buffer.readInt();
            var content = new CodecBuffer(in.copy(in.readerOffset(), readableBytes, true));
            in.skipReadableBytes(readableBytes);

            Class<? extends Packet> packetClass = (Class<? extends Packet>) Class.forName(className);
            var packet = packetClass.getConstructor(CodecBuffer.class).newInstance(content);

            buffer.resetBuffer();

            ctx.fireChannelRead(packet);
        } catch (Exception e) {
            System.err.println("Error while decoding packet " + className);
            e.printStackTrace();
        }
    }
}
