package com.barrage.message;

import com.barrage.transport.Connection;
import com.barrage.protocol.Packet;
import io.netty.channel.ChannelFuture;

/**
 * @author liyang
 * @description:
 * @date 2018/3/16
 */
public abstract class BaseMessage<T extends Packet>  implements Message {

    protected T packet;

    protected Connection connection;

    public BaseMessage(T packet, Connection connection) {
        this.packet = packet;
        this.connection = connection;
    }


    @Override
    public void decode() {

    }

    @Override
    public void encode() {

    }

    public ChannelFuture send() {
        encode();

        return connection.send(packet);
    }
}
