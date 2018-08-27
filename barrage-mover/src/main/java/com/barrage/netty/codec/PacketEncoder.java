

package com.barrage.netty.codec;

import com.barrage.protocol.DouyuPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;



@ChannelHandler.Sharable
public final class PacketEncoder extends MessageToByteEncoder<DouyuPacket> {

    @Override
    protected void encode(ChannelHandlerContext ctx, DouyuPacket packet, ByteBuf out) throws Exception {
        packet.encode(out);
    }
}
