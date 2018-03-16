package com.danmu.protocol;


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
