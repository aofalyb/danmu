package com.barrage.message;

import com.barrage.protocol.DouyuPacket;
import com.barrage.netty.Connection;

/**
 * 心跳消息
 */
public class DouyuHeartbeatMessage extends DouyuMessage {


    private static final String HEART_BEAT_CMD = "type@=mrkl/";

    public DouyuHeartbeatMessage(DouyuPacket packet, Connection connection) {
        super(packet,connection);
    }

    @Override
    public void encode() {
        packet = new DouyuPacket(HEART_BEAT_CMD.getBytes());
    }
}
