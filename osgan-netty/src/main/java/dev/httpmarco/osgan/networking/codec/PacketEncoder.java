package dev.httpmarco.osgan.networking.codec;

import dev.httpmarco.osgan.networking.Packet;
import io.netty5.buffer.Buffer;
import io.netty5.channel.ChannelHandlerContext;
import io.netty5.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty5.handler.codec.MessageToByteEncoder;
import io.netty5.handler.stream.ChunkedInput;
import io.netty5.handler.stream.ChunkedWriteHandler;

import java.nio.charset.StandardCharsets;

public class PacketEncoder extends MessageToByteEncoder<Packet> {
    @Override
    protected Buffer allocateBuffer(ChannelHandlerContext ctx, Packet msg) {
        // amount of chars in class name
        var bytes = Integer.BYTES +
                // class name
                msg.getClass().getName().getBytes(StandardCharsets.UTF_8).length +
                // amount of bytes in buffer
                Integer.BYTES +
                // buffer content
                msg.getBuffer().getOrigin().readableBytes();

        return ctx.bufferAllocator().allocate(bytes);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Packet msg, Buffer out) {
        try {
            var origin = msg.getBuffer().getOrigin();
            var buffer = new CodecBuffer(out);
            var readableBytes = origin.readableBytes();
            
            buffer.writeString(msg.getClass().getName());
            buffer.writeInt(readableBytes);

            origin.copyInto(0, out, out.writerOffset(), readableBytes);
            out.skipWritableBytes(readableBytes);
        } catch (Exception e) {
            System.err.println("Error while encoding packet " + msg.getClass().getName());
            e.printStackTrace();
        }
    }
}
