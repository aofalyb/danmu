package com.barrage.common;

import com.barrage.api.Connection;
import com.barrage.protocol.Packet;

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
