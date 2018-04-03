package com.danmu.common;

import com.danmu.api.Connection;
import com.danmu.protocol.Packet;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author liyang
 * @description:
 * @date 2018/3/16
 */
public abstract class BaseMessage implements Message{

    protected Packet packet;

    private Connection connection;

    public BaseMessage(Packet packet, Connection connection) {
        this.packet = packet;
        this.connection = connection;
    }


    public void send(OnMessageSendListener onMessageSendListener) {



    }
}
