package com.danmu.protocol;


import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author liyang
 * @description:pandatv 弹幕协议
 * @date 2018/3/16
 */
public class PandaPacket implements Packet {


    @Override
    public ByteBuffer encode() {
        return null;
    }

    @Override
    public void decode(SocketChannel socketChannel) throws IOException {

    }
}
