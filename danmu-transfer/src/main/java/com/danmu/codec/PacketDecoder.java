/*
 * (C) Copyright 2015-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *   ohun@live.cn (夜色)
 */

package com.danmu.codec;
import com.danmu.protocol.DouyuPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.TooLongFrameException;

import java.nio.ByteOrder;
import java.util.List;


/**
 * Created by ohun on 2015/12/19.
 * length(4)+cmd(1)+cc(2)+flags(1)+sessionId(4)+lrc(1)+body(n)
 *
 * @author ohun@live.cn
 */
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