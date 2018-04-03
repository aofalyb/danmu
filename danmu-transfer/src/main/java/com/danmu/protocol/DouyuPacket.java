package com.danmu.protocol;


import com.danmu.common.Log;
import io.netty.buffer.ByteBuf;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;


/**
 * @author liyang
 * @description:斗鱼弹幕协议（小端整数），基于《斗鱼弹幕服务器第三方接入协议v1.6.2》
 * @date 2018/3/16
 */
public class DouyuPacket implements Packet {
    //消息长度：4 字节小端整数，表示整条消息（不包括自身）长度（字节数）。消息长度出现两遍，二者相同
    //4 bytes
    private int length;

    public static final short PACKET_TYPE_TO_SERVER = 689;

    public static final short PACKET_FROM_SERVER = 690;


    public static final byte PACKET_TYPE_LOGIN = 0x01;


    public static final byte PACKET_TYPE_JOINGROUP = 0x02;


    public static final byte PACKET_TYPE_HEARTBEAT = 0x03;

    public static final int HEADER_LEN = 12;

    private byte encrypt = 0x00;

    private byte reserved = 0x00;

    private byte ending = 0x00;

    transient byte[] body;

    public DouyuPacket() {
    }

    public DouyuPacket(byte[] body) {
        this.body = body;
        length = this.body.length + 4 + 2 + 1 + 1 + 1;
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





    @Override
    public void encode(ByteBuf out) {
        /*out.order(ByteOrder.LITTLE_ENDIAN);
        out.writeInt(length);
        out.writeInt(length);
        out.writeShort(PACKET_TYPE_TO_SERVER);
        out.writeByte(encrypt);
        out.writeByte(reserved);
        out.writeBytes(getBody());
        out.writeByte(ending);
        body = null;*/

        ByteBuffer byteBuffer = ByteBuffer.allocate(length + 4).order(ByteOrder.LITTLE_ENDIAN);


        byteBuffer.putInt(length);
        byteBuffer.putInt(length);

        byteBuffer.putShort(DouyuPacket.PACKET_TYPE_TO_SERVER);
        byteBuffer.put(encrypt);
        byteBuffer.put(reserved);

        byteBuffer.put(body);
        byteBuffer.put(ending);
        byteBuffer.flip();

        out.writeBytes(byteBuffer.array());



    }

    @Override
    public void decode(ByteBuf byteBuf,int length)  {
        setLength(length);
        byte[] bytes = new byte[4 + 4];
        byteBuf.readBytes(bytes);

        byte[] body = new byte[length - 1 - 4 - 4];

        byteBuf.readBytes(body);
        this.body = body;

        byte ending = byteBuf.readByte();
        Log.d("ending byte -> "+ending);
    }


    @Override
    public String toString() {
        return "{" +
                "length=" + length +
                ", encrypt=" + encrypt +
                ", reserved=" + reserved +
                ", ending=" + ending +
                ", body=" + body.length +
                '}';
    }

}
