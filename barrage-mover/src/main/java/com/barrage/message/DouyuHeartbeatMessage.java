package com.barrage.message;

import com.barrage.protocol.DouyuPacket;
import com.barrage.transport.Connection;
import io.netty.channel.ChannelFuture;

/**
 * 心跳消息
 */
public class DouyuHeartbeatMessage extends BaseMessage<DouyuPacket> {

    private DouyuPacket packet;

    private static final String HEART_BEAT_CMD = "type@=mrkl/";

    public DouyuHeartbeatMessage(DouyuPacket packet, Connection connection) {
        super(packet,connection);
    }

    @Override
    public void encode() {
        packet = new DouyuPacket(HEART_BEAT_CMD.getBytes());
    }


}
