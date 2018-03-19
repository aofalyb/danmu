package com.danmu.common;

import com.danmu.protocol.DouyuPacket;
import com.danmu.protocol.Packet;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liyang
 * @description:斗鱼弹幕协议（小端整数），基于《斗鱼弹幕服务器第三方接入协议v1.6.2》
 * @date 2018/3/16
 */
public class DouyuMessage extends BaseMessage {


    private DouyuPacket douyuPacket = (DouyuPacket) packet;

    protected Map<String,String> attributes = new HashMap();

    public DouyuMessage(Packet packet, SocketChannel channel) {
        super(packet, channel);
    }


    public Message decode() {


        byte[] douyuPacketBody = douyuPacket.getBody();

        if(douyuPacketBody != null){
            attributes = DouyuSerializeUtil.unSerialize2(new String(douyuPacketBody));
        }

        return this;
    }

    public ByteBuffer encode() {

        ByteBuffer buffer = ByteBuffer.allocate(douyuPacket.getLength() + 4).order(ByteOrder.LITTLE_ENDIAN);

        buffer.putInt(douyuPacket.getLength());
        buffer.putInt(douyuPacket.getLength());

        buffer.putShort(DouyuPacket.PACKET_TYPE_TO_SERVER);
        buffer.put(douyuPacket.getEncrypt());
        buffer.put(douyuPacket.getReserved());

        buffer.put(douyuPacket.getBody());
        buffer.put(douyuPacket.getEnding());
        buffer.flip();

        return buffer;
    }


    public Map getAttributes() {
        return attributes;
    }

    private void setAttributes(Map attributes) {
        this.attributes = attributes;
    }
}
