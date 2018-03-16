package com.danmu.codec;

import com.danmu.protocol.DouyuPacket;
import com.danmu.protocol.Packet;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SocketChannel;

/**
 * @author liyang
 * @description:
 * @date 2018/3/16
 */
public class DouyuPacketDecoder {



    public static DouyuPacket decode(SocketChannel channel) throws IOException {

        DouyuPacket douyuPacket = new DouyuPacket();
        ByteBuffer lengthBuffer = ByteBuffer.allocate(4 + 4 + 2 + 2).order(ByteOrder.LITTLE_ENDIAN);
        channel.read(lengthBuffer);
        lengthBuffer.flip();

        if (lengthBuffer.remaining() < 4) {
            return null;
        }

        int contentLength = lengthBuffer.getInt();


        int realLength = contentLength - 4 - 2 -2 - 1;
        ByteBuffer contentBuffer = ByteBuffer.allocate(realLength);

        int read = channel.read(contentBuffer);

        while (read < realLength){

            read += channel.read(contentBuffer);
        }
        douyuPacket.setBody(contentBuffer.array());

        ByteBuffer ending = ByteBuffer.allocate(1);
        channel.read(ending);
        ending.flip();

        return douyuPacket;
    }

}
