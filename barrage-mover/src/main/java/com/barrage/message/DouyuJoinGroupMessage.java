package com.barrage.message;

import com.barrage.protocol.DouyuPacket;
import com.barrage.netty.Connection;
import com.barrage.util.DouyuPacketBuilder;

/**
 * 心跳消息
 */
public class DouyuJoinGroupMessage extends DouyuMessage {


    public DouyuJoinGroupMessage(DouyuPacket packet, Connection connection) {
        super(packet, connection);
    }


    @Override
    public void encode() {
        packet = DouyuPacketBuilder.build(DouyuPacket.PACKET_TYPE_JOINGROUP,connection.getRid());
    }

}
