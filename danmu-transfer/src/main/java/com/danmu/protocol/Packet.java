package com.danmu.protocol;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author liyang
 * @description:
 * @date 2018/3/16
 */
public interface  Packet {


     ByteBuffer encode();

     void decode(SocketChannel socketChannel) throws IOException;
}
