package com.danmu.boot;

import com.danmu.api.Connection;
import com.danmu.codec.DouyuPacketDecoder;
import com.danmu.common.*;
import com.danmu.protocol.DouyuPacket;

import java.io.IOException;
import java.net.InetSocketAddress;
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

    static Connection connection;

    static final String ROOM_ID = "3168536";

    public static void main(String[] args){
        try {
            Selector selector = Selector.open();
            channel = SocketChannel.open();
            channel.configureBlocking(false);
            channel.connect(new InetSocketAddress("openbarrage.douyutv.com",8601));
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
                Log.d("connected server："+channel.getRemoteAddress());

                connection = new Connection(channel,ROOM_ID);

                channel.finishConnect();
                doLogin();
            }

        }

    }

    //发送登录认证消息 78561
    private static void doLogin() {

        DouyuMessage douyuLoginMessage = new DouyuMessage(DouyuPacketBuilder.build(DouyuPacket.PACKET_TYPE_LOGIN,ROOM_ID),connection);

        douyuLoginMessage.send(new OnMessageSendListener() {
            @Override
            public void onSuccess() {
                Log.d("try login...");
                connection.refreshWrite();
            }
            @Override
            public void onError() {
                System.out.println("login error...");
            }
        });

    }


    private static int giftCount = 0;

    private static void handleMessage(SelectionKey key) throws IOException{

        if(key.isReadable()){

            SocketChannel channel  = (SocketChannel) key.channel();


            DouyuPacket douyuPacket = new DouyuPacket();
            douyuPacket.decode(channel);

            DouyuMessage douyuMessage = new DouyuMessage(douyuPacket,connection);
            douyuMessage.decode();


            Map<String, String> attributes = douyuMessage.getAttributes();

            String type = douyuMessage.getAttributes().get("type");

            if("loginres".equals(type)){
                Log.d("login success...");
                DouyuMessage joingroupMessage = new DouyuMessage(DouyuPacketBuilder.build(DouyuPacket.PACKET_TYPE_JOINGROUP,ROOM_ID),connection);

                joingroupMessage.send(new OnMessageSendListener() {
                    @Override
                    public void onSuccess() {
                        Log.d("join a group success...");
                        Log.d("waiting for danmu...");
                    }
                    @Override
                    public void onError() {
                        Log.d("join group error...");
                    }
                });
            }

            if("chatmsg".equals(type)){
                Log.d(douyuMessage.getAttributes().get("nn")+":"+attributes.get("txt"));
            }

            if("dgb".equals(type)){
                Log.d("###"+attributes.get("nn")+"赠送了礼物gfid#["+attributes.get("gfid")+"]"+"###" + ++giftCount + "###");
            }

        }

    }

}
