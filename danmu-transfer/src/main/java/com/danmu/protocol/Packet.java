package com.danmu.protocol;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author liyang
 * @description:
 * @date 2018/3/16
 */
public interface  Packet {


     void encode(ByteBuf out);

     void decode(ByteBuf byteBuf,int length);
}
