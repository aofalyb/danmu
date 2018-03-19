package com.danmu.protocol;


import com.danmu.common.DouyuSerializeUtil;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

/**
 * @author liyang
 * @description:斗鱼弹幕协议（小端整数），基于《斗鱼弹幕服务器第三方接入协议v1.6.2》
 * @date 2018/3/16
 */
public class DouyuPacket extends Packet {
    //消息长度：4 字节小端整数，表示整条消息（不包括自身）长度（字节数）。消息长度出现两遍，二者相同
    //4 bytes
    private int length;

    public static final short PACKET_TYPE_TO_SERVER = 689;

    public static final short PACKET_FROM_SERVER = 690;


    public static final byte PACKET_TYPE_LOGIN = 0x01;


    public static final byte PACKET_TYPE_JOINGROUP = 0x02;


    public static final byte PACKET_TYPE_HEARTBEAT = 0x03;

    private byte encrypt = 0x00;

    private byte reserved = 0x00;

    private byte ending = 0x00;

    transient byte[] body;

    public DouyuPacket() {
    }

    public DouyuPacket(String content) {
        super(content);
        body = content.getBytes();
        length = body.length + 4 + 2 + 1 + 1 + 1;
    }


    public ByteBuffer encode() {
        ByteBuffer buffer = ByteBuffer.allocate(getLength() + 4).order(ByteOrder.LITTLE_ENDIAN);

        buffer.putInt(getLength());
        buffer.putInt(getLength());

        buffer.putShort(DouyuPacket.PACKET_TYPE_TO_SERVER);
        buffer.put(getEncrypt());
        buffer.put(getReserved());

        buffer.put(getBody());
        buffer.put(getEnding());
        buffer.flip();

        return buffer;
    }


    public Map decode() {
        byte[] douyuPacketBody = getBody();

        if(douyuPacketBody != null){
           return DouyuSerializeUtil.unSerialize(new String(douyuPacketBody));
        }

        return new HashMap();
    }




    public int getLength() {
        return length;
    }

    private void setLength(int length) {
        this.length = length;
    }


    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
        this.length = this.body.length;
    }

    public byte getEncrypt() {
        return encrypt;
    }

    public byte getReserved() {
        return reserved;
    }

    public byte getEnding() {
        return ending;
    }

    @Override
    public String toString() {
        return "DouyuPacket{" +
                "length=" + length +
                ", encrypt=" + encrypt +
                ", reserved=" + reserved +
                ", ending=" + ending +
                ", body=" + body.length +
                '}';
    }
}
