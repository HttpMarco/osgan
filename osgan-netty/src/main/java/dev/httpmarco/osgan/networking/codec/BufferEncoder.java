package dev.httpmarco.osgan.networking.codec;

import io.netty5.buffer.Buffer;
import io.netty5.channel.ChannelHandlerContext;
import io.netty5.handler.codec.MessageToByteEncoder;

public class BufferEncoder extends MessageToByteEncoder<Buffer> {
    @Override
    protected Buffer allocateBuffer(ChannelHandlerContext ctx, Buffer msg) {
        // amount of readable bytes
        var bytes = Integer.BYTES +
                // content
                msg.readableBytes();

        return ctx.bufferAllocator().allocate(bytes);
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Buffer msg, Buffer out) {
        try {
            out.writeInt(msg.readableBytes());
            out.writeBytes(msg);
        } catch (Exception e) {
            System.err.println("Error while encoding buffer!");
            e.printStackTrace();
        }
    }
}
