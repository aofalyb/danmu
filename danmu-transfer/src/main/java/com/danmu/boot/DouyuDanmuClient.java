package com.danmu.boot;

import com.alibaba.fastjson.JSON;
import com.danmu.codec.DouyuPacketDecoder;
import com.danmu.common.*;
import com.danmu.protocol.DouyuPacket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Set;

/**
 * @author liyang
 * @description:
 * @date 2018/3/7
 */
public class DouyuDanmuClient {
    static Charset charset = Charset.forName("UTF-8");
    private volatile boolean stop = false;

    static Selector selector = null;

    static SocketChannel channel = null;

    static final String ROOM_ID = "4809";

    public static void main(String[] args){
        try {
            Selector selector = Selector.open();
             channel = SocketChannel.open();
            // 设置为非阻塞模式，这个方法必须在实际连接之前调用(所以open的时候不能提供服务器地址，否则会自动连接)
            channel.configureBlocking(false);
            channel.connect(new InetSocketAddress("openbarrage.douyutv.com",8601));
            //channel.connect(new InetSocketAddress("192.168.1.229",6378));
            channel.register(selector, SelectionKey.OP_CONNECT);

            selector.select();
            Set<SelectionKey> selectionKeys = selector.selectedKeys();

            if(selectionKeys.iterator().hasNext()){
                SelectionKey selectionKey = selectionKeys.iterator().next();
                handleConnect(selectionKey);
            }

            channel.register(selector,SelectionKey.OP_READ);

            while (true){
                selector.select();
                Set<SelectionKey> msgKeys = selector.selectedKeys();

                if(msgKeys.iterator().hasNext()){
                    SelectionKey selectionKey = msgKeys.iterator().next();
                    handleMessage(selectionKey);
                }
            }


        } catch (Exception e){
            e.printStackTrace();
        }

    }


    private static void handleConnect(SelectionKey key) throws IOException{


        if(key.isConnectable()){
            if(channel.isConnectionPending()){
                System.out.println("成功连接服务端："+channel.getRemoteAddress());
                channel.finishConnect();
                doLogin();

            }

        }

    }

    //发送登录认证消息 78561
    private static void doLogin() {

        DouyuMessage douyuLoginMessage = new DouyuMessage(DouyuPacketBuilder.build(DouyuPacket.PACKET_TYPE_LOGIN,ROOM_ID),channel);

        douyuLoginMessage.send(new OnMessageSendListener() {
            public void onSuccess() {
                System.out.println("try login...");
            }

            public void onError() {
                System.out.println("login error...");
            }
        });

    }


    private static void handleMessage(SelectionKey key) throws IOException{

        if(key.isReadable()){

            SocketChannel channel  = (SocketChannel) key.channel();

            DouyuPacket douyuPacket = DouyuPacketDecoder.decode(channel);

            if(douyuPacket == null){
                System.out.println();
                return;
            }

            DouyuMessage douyuMessage = new DouyuMessage(douyuPacket,channel);
            try {
                douyuMessage.decode();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Map<String,String> attributes = douyuMessage.getAttributes();

            String type = attributes.get("type");

            if("loginres".equals(type)){
                System.out.println("login success...");
                DouyuMessage joingroupMessage = new DouyuMessage(DouyuPacketBuilder.build(DouyuPacket.PACKET_TYPE_JOINGROUP,ROOM_ID),channel);

                joingroupMessage.send(new OnMessageSendListener() {
                    public void onSuccess() {
                        System.out.println("join a group success...");
                        System.out.println("waif for danmu...");
                    }

                    public void onError() {
                        System.out.println("join group error...");
                    }
                });
            }

            if("chatmsg".equals(type)){
                System.out.println(attributes.get("nn")+":"+attributes.get("txt"));
            }

            if("dgb".equals(type)){
                System.out.println("###"+attributes.get("nn")+"赠送了礼物gfid#["+attributes.get("gfid")+"]"+"###");
            }


        }


    }

    private  void doWrite(SocketChannel sc,String data) throws IOException{
        byte[] req =data.getBytes();
        ByteBuffer byteBuffer = ByteBuffer.allocate(req.length);
        byteBuffer.put(req);
        byteBuffer.flip();
        sc.write(byteBuffer);
        if(!byteBuffer.hasRemaining()){
            System.out.println("Send 2 client successed");
        }
    }
}
