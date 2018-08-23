package com.barrage.protocol;


import io.netty.buffer.ByteBuf;


/**
 * @author liyang
 * @description:斗鱼弹幕协议（小端整数），基于《斗鱼弹幕服务器第三方接入协议v1.6.2》
 * @date 2018/3/16
 */
public class DouyuPacket implements Packet {
    //消息长度：4 字节小端整数，表示整条消息（不包括自身）长度（字节数）。消息长度出现两遍，二者相同
    //4 bytes

    public static final short PACKET_TYPE_TO_SERVER = 689;
    public static final byte PACKET_TYPE_LOGIN = 0x01;
    public static final byte PACKET_TYPE_JOINGROUP = 0x02;
    public static final byte PACKET_TYPE_HEARTBEAT = 0x03;
    public static final int HEADER_LEN = 12;
    public static final int INT_LEN = 4;
    public static final int SHORT_LEN = 2;
    public static final int BYTE_LEN = 1;

    private byte encrypt = 0x00;
    private byte reserved = 0x00;
    private byte ending = 0x00;
    private int length;
    transient byte[] body;
    private short type;

    public DouyuPacket() {
    }

    public DouyuPacket(byte[] body) {
        this.body = body;
        //这个长度是不包括前四个字节的，文档有误
        length = this.body.length + INT_LEN + SHORT_LEN + BYTE_LEN + BYTE_LEN + BYTE_LEN;
    }




    @Override
    public void encode(ByteBuf out) {

        out.writeIntLE(length);
        out.writeIntLE(length);
        out.writeShortLE(DouyuPacket.PACKET_TYPE_TO_SERVER);
        out.writeByte(encrypt);
        out.writeByte(reserved);
        out.writeBytes(body);
        out.writeByte(ending);

    }

    @Override
    public void decode(ByteBuf byteBuf,int length)  {

        setLength(length);
        //无意义的重复字段 4
        byteBuf.readIntLE();
        //type 2
        type = byteBuf.readShortLE();
        //1
        encrypt = byteBuf.readByte();

        reserved = byteBuf.readByte();

        byte[] body = new byte[length - INT_LEN - SHORT_LEN - BYTE_LEN - BYTE_LEN - BYTE_LEN];
        byteBuf.readBytes(body);
        this.body = body;
        ending = byteBuf.readByte();
    }




    private void setLength(int length) {
        this.length = length;
    }


    public byte[] getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "douyu -> {" +
                "length=" + length +
                ", encrypt=" + encrypt +
                ", reserved=" + reserved +
                ", ending=" + ending +
                ", body=" + body.length +
                '}';
    }


}
