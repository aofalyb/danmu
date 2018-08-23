package com.barrage.protocol;

import io.netty.buffer.ByteBuf;

/**
 * @author liyang
 * @description:
 * @date 2018/3/16
 */
public interface  Packet {


     void encode(ByteBuf out);

     void decode(ByteBuf byteBuf,int length);
}
