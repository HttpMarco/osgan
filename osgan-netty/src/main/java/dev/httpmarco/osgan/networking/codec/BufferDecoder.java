package dev.httpmarco.osgan.networking.codec;

import io.netty5.buffer.Buffer;
import io.netty5.channel.ChannelHandlerContext;
import io.netty5.handler.codec.ByteToMessageDecoder;

public class BufferDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, Buffer in) {
        try {
            var readableBytes = in.readInt();

            if (readableBytes == 0 || in.readableBytes() < readableBytes) {
                return;
            }

            ctx.fireChannelRead(in.copy(in.readerOffset(), readableBytes));
            in.skipReadableBytes(readableBytes);
        } catch (Exception e) {
            System.err.println("Error while decoding buffer!");
            e.printStackTrace();
        }
    }
}
