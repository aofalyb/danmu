
package com.barrage.codec;
import com.barrage.protocol.DouyuPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.TooLongFrameException;

import java.util.List;



public final class PacketDecoder extends ByteToMessageDecoder {
    private static final int maxPacketSize = 10 * 1024;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        decodeFrames(in, out);

    }


    private void decodeFrames(ByteBuf in, List<Object> out) {
        if (in.readableBytes() >= DouyuPacket.HEADER_LEN) {
            //1.记录当前读取位置位置.如果读取到非完整的frame,要恢复到该位置,便于下次读取
            in.markReaderIndex();

            DouyuPacket packet = decodeFrame(in);
            if (packet != null) {
                out.add(packet);
            } else {
                //2.读取到不完整的frame,恢复到最近一次正常读取的位置,便于下次读取
                in.resetReaderIndex();
            }
        }
    }

    private DouyuPacket decodeFrame(ByteBuf in) {

        int readableBytes = in.readableBytes();
        int bodyLength = in.readIntLE();
        if (readableBytes < (bodyLength + 4)) {
            return null;
        }
        if (bodyLength > maxPacketSize) {
            throw new TooLongFrameException("packet body length over limit:" + bodyLength);
        }

        DouyuPacket douyuPacket = new DouyuPacket();
        douyuPacket.decode(in,bodyLength);
        return douyuPacket;
    }

}