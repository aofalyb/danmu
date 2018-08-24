package com.barrage.message;

import com.barrage.protocol.DouyuPacket;
import com.barrage.transport.Connection;
import io.netty.channel.ChannelFuture;

/**
 * 心跳消息
 */
public class DouyuJoinGroupMessage extends BaseMessage<DouyuPacket> {

    public DouyuJoinGroupMessage(DouyuPacket packet, Connection connection) {
        super(packet, connection);
    }

    @Override
    public void decode() {

    }

    @Override
    public ChannelFuture send() {

        return super.send();
    }
}