package com.danmu.common;

import com.danmu.protocol.DouyuPacket;
import com.danmu.protocol.Packet;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SocketChannel;

/**
 * @author liyang
 * @description:
 * @date 2018/3/16
 */
public class DouyuJoingroupMessage extends DouyuMessage {


    public DouyuJoingroupMessage(Packet packet, SocketChannel channel) {
        super(packet, channel);
    }

    public Message decode() {
        return null;
    }
}
