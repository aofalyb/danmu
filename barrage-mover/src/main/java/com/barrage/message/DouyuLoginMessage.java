package com.barrage.message;

import com.barrage.protocol.DouyuPacket;
import com.barrage.transport.Connection;
import com.barrage.util.DouyuPacketBuilder;
import io.netty.channel.ChannelFuture;

/**
 * 心跳消息
 */
public class DouyuLoginMessage extends DouyuMessage {

    public static final int LOGIN_TIME_OUT = 5 * 1000;

    private String rid;

    public DouyuLoginMessage setRid(String rid) {
        this.rid = rid;
        return this;
    }

    public DouyuLoginMessage(DouyuPacket packet, Connection connection) {
        super(packet, connection);
    }

    @Override
    public void encode() {
        packet = DouyuPacketBuilder.build(DouyuPacket.PACKET_TYPE_LOGIN, rid);
    }


}
