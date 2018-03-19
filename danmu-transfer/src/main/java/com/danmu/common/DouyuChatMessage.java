package com.danmu.common;

import com.danmu.protocol.Packet;

import java.nio.channels.SocketChannel;

/**
 * @author liyang
 * @description:
 * @date 2018/3/19
 */
public class DouyuChatMessage extends DouyuMessage {
    public DouyuChatMessage(Packet packet, SocketChannel channel) {
        super(packet, channel);
    }
}
